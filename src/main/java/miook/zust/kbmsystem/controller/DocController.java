package miook.zust.kbmsystem.controller;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.model.FileListing;
import miook.zust.kbmsystem.entity.TDocument;
import miook.zust.kbmsystem.service.DocServiceI;
import miook.zust.kbmsystem.util.QiniuUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//swagger用@RestController进行注解
@Controller
@RequestMapping("doc")
public class DocController {
    @Autowired
    DocServiceI docService;

    @GetMapping("/upload")
    public String toUploadPage(){
        return "upload";
    }
    @PostMapping("/upload")
    public ModelAndView uploadFile(@RequestParam MultipartFile file) throws IOException {
        ModelAndView mav = new ModelAndView();

//        String fileName = docService.uploadFile(file);


        FileListing files = docService.listFiles();
        mav.addObject("fmap",files.items);
        mav.setViewName("upload");
        return mav;
    }
//    @GetMapping("/list")
//    public ModelAndView toList() throws QiniuException {
//        ModelAndView mav = new ModelAndView();
//        FileListing files = docService.listFiles();
//        mav.addObject("fmap",files.items);
//        mav.setViewName("list");
//        return mav;
//    }
    @GetMapping("/delete")
    public String deleteFile(@RequestParam String key) throws QiniuException {
        docService.deleteFile(key);
        return "redirect:list";
    }

    @RequestMapping("likes")
    public ModelAndView getDocLikes(@RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                                    @RequestParam(value = "pageSize", defaultValue = "2") int pageSize) {
        ModelAndView mav = new ModelAndView("likes");
        System.out.println("============================");
        Page<TDocument> docs=docService.getDocLikes(pageNum, pageSize);
        System.out.println("总页数" + docs.getTotalPages());
        System.out.println("当前页是：" + pageNum+1);

        System.out.println("分页数据：");
        Iterator<TDocument> u = docs.iterator();
        while (u.hasNext()){
            System.out.println(u.next().toString());
        }
        mav.addObject("doclikes", docs);
        return mav;
    }

    @RequestMapping("list")
    public ModelAndView getDoclist(@RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "4") int pageSize) {
        ModelAndView mav = new ModelAndView("list");
        System.out.println("============================");
        Page<TDocument> docs=docService.getDoclist(pageNum, pageSize);
        System.out.println("总页数" + docs.getTotalPages());
        System.out.println("当前页是：" + pageNum+1);

        System.out.println("分页数据：");
        Iterator<TDocument> u = docs.iterator();
        while (u.hasNext()){
            System.out.println(u.next().toString());
        }
        mav.addObject("docs", docs);
        return mav;
    }
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private QiniuUtil qiniuUtil;
    @GetMapping("getresults")
    String docssearch(String search, HttpServletRequest request) throws IOException {
        SearchRequest searchRequest = new SearchRequest("es_doc");
        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();
        MatchQueryBuilder matchQueryBuilder= QueryBuilders.matchQuery("content", search);
        sourceBuilder.query(matchQueryBuilder);
        HighlightBuilder highlightBuilder=new HighlightBuilder().field("content").field("virtual").preTags("<strong>").postTags("</strong>").fragmentSize(10).noMatchSize(50);
        sourceBuilder.highlighter(highlightBuilder);

        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);
        HashMap<String,String> dmap=new HashMap<>();
        HashMap<String,String> smap=new HashMap<>();
        SearchResponse searchResponse= restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println("共搜到:"+searchResponse.getHits().getTotalHits()+"条结果!");
        System.out.println("========================================");
        for(SearchHit documentFields : searchResponse.getHits().getHits()){
            Map<String, HighlightField> map = documentFields.getHighlightFields();
            Map<String, Object> sourceAsMap = documentFields.getSourceAsMap();
            System.out.println(map.toString());

            String fileurl=sourceAsMap.get("path").toString();
            String[] a=fileurl.split(",");
            String[] b=a[0].split("/");
            String c=map.toString();
            c= c.substring(c.indexOf("[",11)+2,c.indexOf("]",34));
            System.out.println(c);
            String docname=b[1];
            dmap.put(docname,c);
            smap.put(docname,qiniuUtil.getFinalUrl(docname));
            request.setAttribute("smap",smap);
            request.setAttribute("dmap",dmap);

            System.out.println("========================================");
        }



        return "search";
    }
}
