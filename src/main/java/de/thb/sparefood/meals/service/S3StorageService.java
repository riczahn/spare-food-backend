package de.thb.sparefood.meals.service;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.net.URI;

@ApplicationScoped
public class S3StorageService implements StorageService {
  private static final String FILE_SERVER = "https://s3.hidrive.strato.com";
  private static final String S3_BUCKET_NAME = "images-mux";

  private static final StaticCredentialsProvider CREDENTIALS_PROVIDER =
      StaticCredentialsProvider.create(
          AwsBasicCredentials.create(
              "AHS4GQ7B4DRUATRS5Q2O", "R1xmlDPCSWV66pjrJHL2hHWUqdYDma5lp+DM6/qq"));

  private static final S3Client S3_CLIENT =
      S3Client.builder()
          .endpointOverride(URI.create(FILE_SERVER))
          .region(Region.EU_CENTRAL_1)
          .credentialsProvider(CREDENTIALS_PROVIDER)
          .build();

  @Override
  public String saveFile(File file, String key) {
    PutObjectRequest uploadRequest =
        PutObjectRequest.builder().bucket(S3_BUCKET_NAME).key(key).build();

    S3_CLIENT.putObject(uploadRequest, RequestBody.fromFile(file));
    return key;
  }

  @Override
  public byte[] getFileAsBytes(String key) {
    GetObjectRequest objectRequest =
        GetObjectRequest.builder().bucket(S3_BUCKET_NAME).key(key).build();

    ResponseBytes<GetObjectResponse> objectBytes = S3_CLIENT.getObjectAsBytes(objectRequest);
    return objectBytes.asByteArray();
  }

  @Override
  public void deleteFile(String key) {
    DeleteObjectRequest objectRequest =
        DeleteObjectRequest.builder().bucket(S3_BUCKET_NAME).key(key).build();
    S3_CLIENT.deleteObject(objectRequest);
  }
}
