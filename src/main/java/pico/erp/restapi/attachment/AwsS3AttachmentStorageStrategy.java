package pico.erp.restapi.attachment;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import pico.erp.attachment.item.AttachmentItemInfo;
import pico.erp.attachment.storage.AttachmentStorageKey;
import pico.erp.attachment.storage.AttachmentStorageStrategy;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class AwsS3AttachmentStorageStrategy implements AttachmentStorageStrategy {

  final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

  @Autowired
  AmazonS3 amazonS3Client;

  @Autowired
  AwsS3AttachmentConfiguration configuration;

  @Override
  public AttachmentStorageKey copy(AttachmentStorageKey storageKey) {
    AttachmentStorageKey copiedKey = createStorageKey(storageKey);
    amazonS3Client.copyObject(
      new CopyObjectRequest(configuration.getBucketName(), storageKey.getValue(),
        configuration.getBucketName(), copiedKey.getValue())
    );
    return copiedKey;
  }

  private AttachmentStorageKey createStorageKey(AttachmentStorageKey storageKey) {
    String key = String
      .format("%s/%s", formatter.format(OffsetDateTime.now()), UUID.randomUUID());
    return AttachmentStorageKey.from(key);
  }

  private AttachmentStorageKey createStorageKey(AttachmentItemInfo info) {
    String key = String
      .format("%s/%s", formatter.format(OffsetDateTime.now()), info.getId().getValue());
    return AttachmentStorageKey.from(key);
  }

  @Override
  public boolean exists(AttachmentStorageKey storageKey) {
    return amazonS3Client.doesObjectExist(configuration.getBucketName(), storageKey.getValue());
  }

  @SneakyThrows
  @Override
  public URI getUri(AttachmentStorageKey storageKey) {
    return amazonS3Client.generatePresignedUrl(
      configuration.getBucketName(),
      storageKey.getValue(),
      new Date(OffsetDateTime.now().plusMinutes(10).toInstant().toEpochMilli())
    ).toURI();
  }

  @Override
  public boolean isUriSupported() {
    return true;
  }

  @Override
  public InputStream load(AttachmentStorageKey storageKey) {
    S3Object object = amazonS3Client.getObject(
      new GetObjectRequest(configuration.getBucketName(), storageKey.getValue())
    );
    return object.getObjectContent();
  }

  @Override
  public void remove(AttachmentStorageKey storageKey) {
    amazonS3Client.deleteObject(
      new DeleteObjectRequest(configuration.getBucketName(), storageKey.getValue())
    );
  }

  @SneakyThrows
  @Override
  public AttachmentStorageKey save(AttachmentItemInfo info, InputStream inputStream) {
    AttachmentStorageKey storageKey = createStorageKey(info);
    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentLength(info.getContentLength());
    metadata.setContentType(info.getContentType());
    metadata.setContentDisposition(
      String.format(
        "attachment; filename=\"%s\";",
        URLEncoder.encode(info.getName(), "UTF-8").replaceAll("\\+", " ")
      )
    );

    amazonS3Client.putObject(
      new PutObjectRequest(
        configuration.getBucketName(),
        storageKey.getValue(),
        inputStream,
        metadata
      )
    );
    return storageKey;
  }


}
