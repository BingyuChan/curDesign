package miook.zust.kbmsystem.service;

import com.qiniu.common.QiniuException;
import com.qiniu.storage.model.FileListing;
import miook.zust.kbmsystem.dto.DocDto;
import miook.zust.kbmsystem.entity.TDocument;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface DocServiceI {
    public List<DocDto> uploadFile(MultipartFile file, String userName) throws IOException;
    public FileListing listFiles() throws QiniuException;
    public void deleteFile(String key) throws QiniuException;
    public Page<TDocument> getDocLikes(int pageNum, int pageSize);
    public Page<TDocument> getDoclist(int pageNum, int pageSize);
    List<DocDto> getMyDoc(String userName);
    List<DocDto> getMyCollection(String userName);
    void saveToMyDoc(String fileName, String userName);
}
