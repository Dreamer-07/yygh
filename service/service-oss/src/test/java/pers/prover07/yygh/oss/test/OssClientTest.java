package pers.prover07.yygh.oss.test;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname OssClientTest
 * @Description TODO
 * @Date 2021/12/5 15:33
 * @Created by Prover07
 */
@SpringBootTest
public class OssClientTest {

    @Test
    public void testCreateBucket(){
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = "LTAI5tMCjk6BE7UJ5TQHV2P1";
        String accessKeySecret = "xTT5yQcSncqDzTBHLXC8egTkWii4tw";
        // 填写Bucket名称，例如examplebucket。
        String bucketName = "yygh-test-oss-bucket";

        OSS ossClient = null;
        try {
            // 创建OSSClient实例。
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 创建存储空间。
            ossClient.createBucket(bucketName);
        } catch (OSSException e){
            e.printStackTrace();
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }
    }
}
