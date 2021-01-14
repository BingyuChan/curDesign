package miook.zust.kbmsystem.controller;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import miook.zust.kbmsystem.dao.DocDao;
import miook.zust.kbmsystem.dto.DocDto;
import miook.zust.kbmsystem.dto.UserDto;
import miook.zust.kbmsystem.entity.TDocument;
import miook.zust.kbmsystem.service.DocServiceI;
import miook.zust.kbmsystem.service.UserServiceI;
import miook.zust.kbmsystem.util.QiniuUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;


//swagger用@RestController进行注解
//@Api(tags = "用户管理模块接口")
@Controller
//@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    UserServiceI userServiceI;
    @Autowired
    DocServiceI docServiceI;
    @Autowired
    DocDao docDao;
    @Autowired
    QiniuUtil qiniuUtil;
    String un;
    @GetMapping("login")
    public String login(){
        return "login";
    }

//    @ApiOperation(value = "用户名密码",notes = "根据用户名密码登录")
    @GetMapping("doLogin")
    public ModelAndView login(@RequestParam("loginName")String loginName,
                              @RequestParam("password") String password,
//                              点赞排行参数 pNum,pSize
                              @RequestParam(value = "pNum", defaultValue = "0") int pNum,
                              @RequestParam(value = "pSize",defaultValue = "5") int pSize,
//                              文档分页显示参数 pageNum,pageSize
                              @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "20")int pageSize,
                              HttpServletRequest request) throws UnsupportedEncodingException, QiniuException {
        ModelAndView mav = new ModelAndView();
        UserDto userDto = new UserDto();
        userDto.setLoginName(loginName);
        un=loginName;
        userDto.setPassword(password);
        FileListing fileListing=qiniuUtil.listfile();
        HashMap<String,String> fmap=new HashMap<>();
        FileInfo[] item=fileListing.items;
        for(FileInfo fileInfo : item){
            fmap.put(fileInfo.key,qiniuUtil.getFinalUrl(fileInfo.key));
        }
        request.setAttribute("fmap",fmap);
        if(userServiceI.login(userDto))
        {   mav.setViewName("index");
//            点赞排行【另起div】
            Page<TDocument> doclikes=docServiceI.getDocLikes(pNum, pSize);
            mav.addObject("doclikes", doclikes);

//            首页显示文档并分页
            Page<TDocument> docs=docServiceI.getDoclist(pageNum, pageSize);
            mav.addObject("docs", docs);
            mav.addObject("username",userDto.getLoginName());
        }
        else
            mav.setViewName("error");
    return mav;
    }

//    @ApiOperation(value = "用户名密码",notes = "根据用户名密码注册")
    @GetMapping("doRegist")
    public ModelAndView register(@RequestParam("loginName")String loginName,
                                 @RequestParam("password") String password){
        ModelAndView mav = new ModelAndView("login");
        UserDto userDto =new UserDto();
        userDto.setLoginName(loginName);
        userDto.setPassword(password);
        userServiceI.addUser(userDto);
        return mav;
    }

    @GetMapping("getDocs")
    public ModelAndView getMyDoc(@RequestParam("username") String userName){
        ModelAndView mav = new ModelAndView("mydocument");
        List<DocDto> docDtos = docServiceI.getMyDoc(userName);
        mav.addObject("docDtos",docDtos);
        return mav;
    }

    @GetMapping("getCollection")
    public ModelAndView getMyCollection(@RequestParam("username") String userName){
        ModelAndView mav = new ModelAndView("mycollection");
        List<DocDto> docDtoCollection = docServiceI.getMyCollection(userName);
        mav.addObject("docDtoCollection",docDtoCollection);
        return mav;
    }

    @RequestMapping("likes")
    public ModelAndView getDocLikes(@RequestParam(value = "pNum", defaultValue = "0") int pNum,
                                    @RequestParam(value = "pSize", defaultValue = "5") int pSize) {
        ModelAndView mav = new ModelAndView("index");
        Page<TDocument> doclikes=docServiceI.getDocLikes(pNum, pSize);
        mav.addObject("doclikes", doclikes);
        return mav;
    }
    @RequestMapping("list")
    public ModelAndView getDoclist(@RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                                   @RequestParam(value = "pageSize", defaultValue = "20") int pageSize) {
        ModelAndView mav = new ModelAndView("index");
        Page<TDocument> docs=docServiceI.getDoclist(pageNum,pageSize);
        mav.addObject("docs", docs);
        return mav;
    }


    @GetMapping("toUpload")
    public String toUpload(){
        return "release";
    }

    @PostMapping("/upload")
    public ModelAndView uploadFile(@RequestParam MultipartFile file) throws IOException {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("mydocument");
        List<DocDto> docDtos = docServiceI.uploadFile(file,un);
        mav.addObject("docDtos",docDtos);
        String key=file.getOriginalFilename();
        FileInputStream is=(FileInputStream)file.getInputStream();
        qiniuUtil.uploadfile(is,key);

        return mav;
    }
}