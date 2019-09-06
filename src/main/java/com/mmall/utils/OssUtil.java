package com.mmall.utils;


import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectResult;
import org.apache.commons.lang3.StringUtils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class OssUtil {
  private final static String accessKeyId = "";
  private final static String accessKeySecret = "";
  private final static String endpoint="";
  private final static String bucketName="";


  public static String getImageUrl(InputStream inputStream)throws Exception {
      if (inputStream == null) {
          return null;
      }
      OSS ossCilent = new OSSClient(endpoint, accessKeyId, accessKeySecret);  //生成OSS客户端
      try {
          SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
          String format = sdf.format(new Date());
          String uuid = UUID.randomUUID().toString().replace("-", "");
          String objectName = format + "/" + uuid + ".jpg";

          ossCilent.putObject(bucketName, objectName, inputStream);
          inputStream.close();
          ossCilent.shutdown();
          return "https://cybimage.oss-cn-shenzhen.aliyuncs.com/" + objectName;
      } catch (Exception e) {
          e.printStackTrace();
          return null;
      }finally {
          inputStream.close();
          ossCilent.shutdown();
      }
  }



    public static void main(String[] args) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        OSS ossCilent=new OSSClient(endpoint,accessKeyId,accessKeySecret);  //生成OSS客户端
        String fileName="D:\\新建文件夹\\54ea9c23dba31-1.jpg";
        FileInputStream inputStream=new FileInputStream(new File(fileName));
        String format = sdf.format(new Date());
        String objectName=format+"/"+"uuid.jpg";
       ossCilent.putObject(bucketName, objectName, inputStream);

        inputStream.close();
        ossCilent.shutdown();
    }


}
