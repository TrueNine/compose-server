package com.truenine.component.oss.amazon;

import com.google.gson.Gson;


public class S3PolicyCreator {
  private S3PolicyCreator() {
  }


  public static S3BuilderChain builder() {
    return new S3BuilderChain();
  }

  public static S3BuilderChain publicBucket(String bucketName) {
    S3PrincipalArgs p = new S3PrincipalArgs();
    p.getAws().add("*");
    return new S3BuilderChain()
      .addStatement(S3StatementBuilder.builder()
        .principal(p)
        .addAction(S3Policies.Bucket.LIST_MUL_UPLOADS)
        .addAction(S3Policies.Bucket.LIST)
        .addAction(S3Policies.Bucket.GET_LOCATION)
        .addResource(bucketName)
      )
      .addStatement(S3StatementBuilder.builder()
        .principal(p)
        .addAction(S3Policies.Obj.GET)
        .addAction(S3Policies.Obj.LIST_MUL_UPLOAD_PARTS)
        .addAction(S3Policies.Obj.PUT)
        .addAction(S3Policies.Obj.ABORT_MUL_UPLOAD)
        .addAction(S3Policies.Obj.DEL)
        .addResource(bucketName + "/*")
      );
  }

  public static S3BuilderChain publicBucketAndReadOnly(String bucketName) {
    S3PrincipalArgs p = new S3PrincipalArgs();
    p.getAws().add("*");
    return new S3BuilderChain()
      .addStatement(S3StatementBuilder.builder()
        .principal(p)
        .addAction(S3Policies.Bucket.LIST)
        .addAction(S3Policies.Bucket.GET_LOCATION)
        .addResource(bucketName)
      )
      .addStatement(S3StatementBuilder.builder()
        .principal(p)
        .addAction(S3Policies.Obj.GET)
        .addResource(bucketName + "/*")
      );
  }

  public static class S3BuilderChain {

    private final S3Args RULE;

    private final Gson GSON = new Gson();


    private S3BuilderChain() {
      this.RULE = new S3Args();
      RULE.setVersion("2012-10-17");
    }

    public S3BuilderChain addStatement(S3StatementBuilder builder) {
      RULE.getStatement().add(builder.statement());
      return this;
    }

    public S3BuilderChain version(String version) {
      RULE.setVersion(version);
      return this;
    }


    public String json() {
      return GSON.toJson(RULE, S3Args.class);
    }
  }

  public static class S3StatementBuilder {

    private final S3StatementArgs S = new S3StatementArgs();

    private S3StatementBuilder() {
    }

    public static S3StatementBuilder builder() {
      return new S3StatementBuilder();
    }

    private S3StatementArgs statement() {
      return S;
    }

    public S3StatementBuilder principal(S3PrincipalArgs principal) {
      S.setPrincipal(principal);
      return this;
    }

    public S3StatementBuilder addResource(String resource) {
      S.getResource().add("arn:aws:s3:::" + resource);
      return this;
    }

    public S3StatementBuilder addAction(String action) {
      S.getAction().add("s3:" + action);
      return this;
    }

    public S3StatementBuilder effect(String effect) {
      S.setEffect(effect);
      return this;
    }


    public S3StatementBuilder build() {
      return this;
    }
  }
}
