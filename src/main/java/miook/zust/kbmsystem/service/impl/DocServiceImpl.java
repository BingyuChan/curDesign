package miook.zust.kbmsystem.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import miook.zust.kbmsystem.dao.CollectionDao;
import miook.zust.kbmsystem.dao.DocDao;
import miook.zust.kbmsystem.dao.MyDocDao;
import miook.zust.kbmsystem.dao.UserDao;
import miook.zust.kbmsystem.dto.DocDto;
import miook.zust.kbmsystem.dto.MyDocDto;
import miook.zust.kbmsystem.entity.TCollection;
import miook.zust.kbmsystem.entity.TDocument;
import miook.zust.kbmsystem.entity.TMyDoc;
import miook.zust.kbmsystem.service.DocServiceI;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocServiceImpl implements DocServiceI {
    @Autowired
    DocDao docDao;
    @Autowired
    UserDao userDao;
    @Autowired
    MyDocDao myDocDao;
    @Autowired
    CollectionDao collectionDao;
    private String ACCESS_KEY="v38Wg5Hpcp8DKAa4G0qi_R0FGc1IQjExnwsXzVc7";
    private String SECRET_KEY="-62Xq-o-XH-FcjfQpif_6lGeWT5Q9I2FIW2AOl0k";
    private String bucket="openfile-zyk";
    Auth auth = Auth.create(ACCESS_KEY,SECRET_KEY);
    Zone z = Zone.autoZone();
    Configuration cfg = new Configuration(z);
    UploadManager uploadManager = new UploadManager(cfg);
    BucketManager bucketManager = new BucketManager(auth, cfg);
    String key = null;
    String upToken = auth.uploadToken(bucket);

    @Override
    public List<DocDto> getMyDoc(String userName) {
//        查询tuser表，通过username找到user后再取其id
        int userId = userDao.findByLoginName(userName).getId();
//        查询tuser_doc_mydoc表，通过userId查到对映关系，返回关系列表
        List<TMyDoc> tMyDocs = myDocDao.findAllByUserId(userId);
//        初始化文档列表
        List<TDocument> tDocuments = new ArrayList<TDocument>();
//        循环关系列表，每次循环通过关系列表获取的docId查找tDocument实体，添加进文档列表
        for(TMyDoc tMyDoc : tMyDocs) {
            System.out.println(tMyDoc.getDocId());
            System.out.println("docId:"+docDao.findById(tMyDoc.getDocId()));
            tDocuments.add(docDao.findById(tMyDoc.getDocId()));
        }
        return e2d(tDocuments);
    }

    @Override
    public List<DocDto> getMyCollection(String userName) {
//        查询tuser表，通过username找到user后再取其id
        int userId = userDao.findByLoginName(userName).getId();
//        查询tuser_doc_mydoc表，通过userId查到对映关系，返回关系列表
        List<TCollection> tCollections = collectionDao.findAllByUserId(userId);
//        初始化文档列表
        List<TDocument> tDocuments = new ArrayList<TDocument>();
//        循环关系列表，每次循环通过关系列表获取的docId查找tDocument实体，添加进文档列表
        for(TCollection tCollection : tCollections) {
            System.out.println(tCollection.getDocId());
            System.out.println("docId:"+docDao.findById(tCollection.getDocId()));
            tDocuments.add(docDao.findById(tCollection.getDocId()));
        }
        return e2d(tDocuments);
    }

    @Override
    public void saveToMyDoc(String fileName,String userName) {
//        获取文件id
        int docId = docDao.findByName(fileName).getId();
//        获取用户id
        int userId = userDao.findByLoginName(userName).getId();
        MyDocDto myDocDto = new MyDocDto();
        myDocDto.setUserId(userId);
        myDocDto.setDocId(docId);
        myDocDao.saveAndFlush(md2e(myDocDto));
    }

    @Override
    public List<DocDto> uploadFile(MultipartFile file, String userName) throws IOException{
//            FileInputStream is = (FileInputStream) file.getInputStream();
//            Response response = uploadManager.put(is,key,upToken,null,null);

//      MutilpartFile转File
        File f;
        InputStream ins = file.getInputStream();
        f=new File(file.getOriginalFilename());
        inputStreamToFile(ins, f);
        System.out.println(f.getName());
        System.out.println(f.getAbsolutePath());

        Response response = uploadManager.put(f,key,upToken);

        DocDto docDto = new DocDto();
        String fileName = f.getName();
        fileName = fileName.substring(fileName.indexOf(""),fileName.indexOf("."));

        System.out.println(fileName);
//        将文档写入数据库
        docDto.setName(f.getName());
        docDto.setLikes("0");
        docDto.setCollection("0");
        TDocument tDocument = d2e(docDto);
        docDao.save(tDocument);
//        获取docId
        int docId = docDao.findByName(f.getName()).getId();
//        获取userId
        int userId = userDao.findByLoginName(userName).getId();
        MyDocDto myDocDto = new MyDocDto();
        myDocDto.setUserId(userId);
        myDocDto.setDocId(docId);
        myDocDao.saveAndFlush(md2e(myDocDto));

        List<TMyDoc> tMyDocs = myDocDao.findAllByUserId(userId);
//        初始化文档列表
        List<TDocument> tDocuments = new ArrayList<TDocument>();
//        循环关系列表，每次循环通过关系列表获取的docId查找tDocument实体，添加进文档列表
        for(TMyDoc tMyDoc : tMyDocs) {
            System.out.println(tMyDoc.getDocId());
            System.out.println("docId:"+docDao.findById(tMyDoc.getDocId()));
            tDocuments.add(docDao.findById(tMyDoc.getDocId()));
        }



        DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        System.out.println(putRet.key);
        System.out.println(putRet.hash);
        return e2d(tDocuments);
    }

    private TDocument d2e(DocDto docDto) {
        TDocument tDocument = new TDocument();
        BeanUtils.copyProperties(docDto,tDocument);
        return tDocument;
    }

    private TMyDoc md2e(MyDocDto myDocDto) {
        TMyDoc tMyDoc = new TMyDoc();
        BeanUtils.copyProperties(myDocDto,tMyDoc);
        return tMyDoc;
    }

    @Override
    public FileListing listFiles() throws QiniuException {
        return bucketManager.listFiles(bucket,null,null,1000,null);
    }

    @Override
    public void deleteFile(String key) {
        try {
            bucketManager.delete(bucket,key);
        } catch (QiniuException e) {
            System.out.println(e.code());
            System.out.println(e.response.toString());
        }
    }

    @Override
    public Page<TDocument> getDocLikes(int pageNum, int pageSize) {
        Sort sort =Sort.by(Sort.Direction.DESC,"likes");
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);
        Page<TDocument> tDocuments = docDao.findAll(pageable);
        System.out.println("----likes------");
        for (TDocument docitem : tDocuments){
           String fileName =docitem.getName();
           if(fileName.indexOf(".")>-1)
                fileName = fileName.substring(fileName.indexOf(""),fileName.indexOf("."));
            System.out.println(fileName);
        }
        return tDocuments;
    }

    @Override
    public Page<TDocument> getDoclist(int pageNum, int pageSize) {
        Sort sort =Sort.by(Sort.Direction.DESC,"collection");
        Pageable pageable = PageRequest.of(pageNum,pageSize,sort);
        Page<TDocument> tDocuments = docDao.findAll(pageable);
        System.out.println("----docList------");
        for (TDocument docitem : tDocuments){
            String fileName =docitem.getName();
            if(fileName.indexOf(".")>-1)
                fileName = fileName.substring(fileName.indexOf(""),fileName.indexOf("."));
            System.out.println(fileName);
        }
        return tDocuments;
    }

    private DocDto e2d(TDocument tDoc){
        DocDto docDto=new DocDto();
        BeanUtils.copyProperties(tDoc,docDto);
        return docDto;
    }

    private List<DocDto> e2d(List<TDocument> tDocs){
        List<DocDto> docDtos=new ArrayList<DocDto>();
        if(tDocs==null||tDocs.size()==0)
            return docDtos;
        for(TDocument tDoc:tDocs)
            docDtos.add(e2d(tDoc));
        return docDtos;
    }


    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
