# weeb4j
[ ![Download](https://api.bintray.com/packages/natanbc/maven/weeb4j/images/download.svg) ](https://bintray.com/natanbc/maven/weeb4j/_latestVersion)

Java wrapper for the weeb.sh API

## Usage

```java
Weeb4J api = new Weeb4J.Builder().setToken(TokenType.WOLKE /* or BEARER */, "my_token").build();

api.getImageTypes().async(types->{
  api.getRandomImage(types.get(0), null, HiddenMode.DEFAULT, NsfwFilter.NO_NSFW, FileType.PNG).async(image->{
    image.download().async(bytes->{
      System.out.println("Downloaded image " + image.getId() + ", with " + bytes.length + " bytes");
    });
  });
});
```

Additional information is available in the javadocs
