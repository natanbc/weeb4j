# weeb4j
[ ![Download](https://api.bintray.com/packages/natanbc/maven/weeb4j/images/download.svg) ](https://bintray.com/natanbc/maven/weeb4j/_latestVersion)

Java wrapper for the weeb.sh API

## Basic Usage

```java
Weeb4J api = new Weeb4J.Builder().setToken(TokenType.WOLKE /* or BEARER */, "my_token").build();

//async call
api.getImageTypes().async(data->{
    //blocking call
    Image image = api.getRandomImage(data.getTypes().get(0), null, HiddenMode.DEFAULT, NsfwFilter.NO_NSFW, FileType.PNG).execute();
    try {
        //futures are supported too
        byte[] bytes = image.download().submit().get();
        System.out.println("Downloaded image " + image.getId() + ", with " + bytes.length + " bytes");
    } catch(InterruptedException|ExecutionException e) {
        e.printStackTrace();
    }
});
```

Additional information is available in the javadocs
