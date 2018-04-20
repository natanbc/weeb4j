# weeb4j
[ ![Download](https://api.bintray.com/packages/natanbc/maven/weeb4j/images/download.svg) ](https://bintray.com/natanbc/maven/weeb4j/_latestVersion)

Java wrapper for the weeb.sh API

## Images 

```java
Weeb4J api = new Weeb4J.Builder().setToken(TokenType.WOLKE /* or BEARER */, "my_token").build();

ImageProvider imageProvider = api.getImageProvider();

//async call
imageProvider.getImageTypes().async(data->{
    //blocking call
    Image image = imageProvider.getRandomImage(data.getTypes().get(0), null, HiddenMode.DEFAULT, NsfwFilter.NO_NSFW, FileType.PNG).execute();
    try {
        //futures are supported too
        byte[] bytes = image.download().submit().get();
        System.out.println("Downloaded image " + image.getId() + ", with " + bytes.length + " bytes");
    } catch(InterruptedException|ExecutionException e) {
        e.printStackTrace();
    }
});
```

## Image Generation

```java
ImageGenerator imageGenerator = api.getImageGenerator();

byte[] image = imageGenerator.generateAwoo(Color.BLUE, Color.RED).execute();
```

## Reputation

```java
ReputationManager reputationManager = api.getReputationManager();

reputationManager.giveReputation(to, from).async();
reputationManager.resetReputation(to).async();
```

## Settings

```java
SettingManager settingManager = api.getSettingManager();

settingManager.getSetting("type", "id").async(setting->{
    JSONObject data = setting == null ? defaultValues : setting.getData();
    data.put("key", "value");
    settingManager.saveSetting("type", "id", data).async();
});
```

It's recommended to cache settings for 1-5 minutes to avoid spamming the API, specially on frequently
accessed settings. To do so, use a SettingCache instance, along with either `Weeb4J.Builder#setSettingCache`
or `SettingManager#setSettingCache`. The artifacts `weeb4j-setting-cache-guava` and `weeb4j-setting-cache-caffeine`
provide implementations using [guava](https://github.com/google/guava) and [caffeine](https://github.com/ben-manes/caffeine), respectively.