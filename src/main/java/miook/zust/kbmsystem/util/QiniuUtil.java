package miook.zust.kbmsystem.util;


import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class QiniuUtil {
    private String ACCESS_KEY="v38Wg5Hpcp8DKAa4G0qi_R0FGc1IQjExnwsXzVc7";
    private String SECRET_KEY="-62Xq-o-XH-FcjfQpif_6lGeWT5Q9I2FIW2AOl0k";
    private String BUCKET_NAME="openfile-zyk";
    Auth auth=Auth.create(ACCESS_KEY,SECRET_KEY);
    public String getUpToken(){
        return auth.uploadToken(BUCKET_NAME);
    }
    Zone z=Zone.autoZone();
    Configuration c=new Configuration(z);
    BucketManager bucketManager=new BucketManager(auth,c);
    UploadManager uploadManager=new UploadManager(c);
    public void uploadfile(FileInputStream is,String key) throws QiniuException {
        uploadManager.put(is,key,getUpToken(),null,null);
    }
    public FileListing listfile() throws QiniuException {
       return bucketManager.listFiles(BUCKET_NAME,null,null,1000,null);
    }

    public void  delete(String key) throws QiniuException {
        bucketManager.delete(BUCKET_NAME,key);
    }
    public String getFinalUrl(String key) throws UnsupportedEncodingException{
        String fileName=key;
        String domainOfBucket="qchh7rk2y.bkt.clouddn.com";
        String encodedFileName= URLEncoder.encode(fileName,"utf-8").replace("+","%20");
        String publicUrl=String.format("%s/%s",domainOfBucket,encodedFileName);
        return publicUrl;
    }


}
