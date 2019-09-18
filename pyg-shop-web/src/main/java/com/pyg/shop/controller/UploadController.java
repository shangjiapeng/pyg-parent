package com.pyg.shop.controller;

import com.pyg.entity.ResultInfo;
import com.pyg.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;//文件服务器的的地址

    @RequestMapping("/upload")
    public ResultInfo upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();//获取文件全名
        String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);//得到扩展名
        try {
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
            String fileId = client.uploadFile(file.getBytes(), extName);
            String url=FILE_SERVER_URL+fileId;//图片上传后服务器返回的完整的地址
            return new ResultInfo(true,url);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false,"");
        }

    }

}
