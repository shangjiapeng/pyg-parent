package com.shang.demo;

import org.csource.fastdfs.*;

public class TestDemo {
    public static void main(String[] args) throws Exception {
        //1 加载配置文件
        ClientGlobal.init("D:\\IDEA\\pyg-parent\\fastDFSDemo\\src\\main\\resources\\fdfs_client.conf");
        //2 创建管理者客户端
        TrackerClient trackerClient = new TrackerClient();
        //3 通过客户端连接管理者服务端
        TrackerServer trackerServer = trackerClient.getConnection();
        //4 声明存储的服务端
        StorageServer storageServer=null;
        //5 构建存储服务器的客户端对象
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        //6 上传图片文件
        String[] strings = storageClient.upload_file("E:\\a.jpg", "jpg", null);
        //7 显示上传的结果信息
        for (String string : strings) {
            System.out.println(string);
        }
    }
}
