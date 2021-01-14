package miook.zust.kbmsystem.controller;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import io.swagger.annotations.Api;
import miook.zust.kbmsystem.util.QiniuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

@Api(tags = "主页与预览下载")
@Controller
@RequestMapping("index")
public class IndexController {
    @Autowired
    QiniuUtil qiniuUtil;
    @GetMapping("getIndex")
    public String list(HttpServletRequest request) throws QiniuException, UnsupportedEncodingException {
        FileListing fileListing=qiniuUtil.listfile();
        HashMap<String,String> fmap=new HashMap<>();
        FileInfo[] item=fileListing.items;
        for(FileInfo fileInfo : item){
            fmap.put(fileInfo.key,qiniuUtil.getFinalUrl(fileInfo.key));
        }
        request.setAttribute("fmap",fmap);
        return"index";
    }
    @GetMapping("getRelease")
    public String release(){
        return "release";
    }
    @GetMapping("getCollection")
    public String collection(){
        return "collection";
    }
    @GetMapping("getMyUpload")
    public String myUpload(){
        return "myupload";
    }
}
