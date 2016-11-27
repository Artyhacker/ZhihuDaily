# 开发笔记


---

## 知乎日报API解析
https://github.com/izzyleung/ZhihuDailyPurify/wiki/%E7%9F%A5%E4%B9%8E%E6%97%A5%E6%8A%A5-API-%E5%88%86%E6%9E%90

---

## Day01 Splash

1. ButterKnife依赖：
    ```
    compile 'com.jakewharton:butterknife:8.4.0'
    annotationProcessor 'com.jakewharton:butterknife-compiler:8.4.0'
    ```
    
2. 布局文件：ImageView图片、TextView版权信息
    - ImageView中，设置``android:scaleType="fitXY"``可令图片填充屏幕。
    - 去除标题栏：在AppTheme中添加：
    
        ```xml
        <item name="windowActionBar">false</item>
        <item name="windowNoTitle">true</item>
        ```
3. 使用ButterKnife注入，记得在setContentView后面加上``ButterKnife.bind(this);``
4. 在子线程中发出网络请求，创建StreamUtil工具类，将InputStream转为String，解析JSON。
5. 将解析出的图片网址转为Bitmap： ``bitmap = BitmapFactory.decodeStream(new URL(splashImageUrl).openConnection().getInputStream());``
6. 在主线程中更新UI：``runOnUiThread();``
7. 开启另一个子线程，sleep2秒之后，跳转到MainActivity，并finish此SplashActivity。

Todo：
1. ~~使用开源框架优化网络请求及JSON解析~~

---

## Day02 获得新闻条目数据并显示到ListView

1. ListView
    - 在布局文件中添加ListView，并初始化控件；创建条目的布局文件item_view
    - 创建NewsBean
    - 创建适配器继承BaseAdapter，适配器的构造函数接收Context和``ArrayList<NewsBean>``两个参数，在getView方法中返回view。
    - 在Activity中初始化适配器并设置给ListView
    - 在子线程中进行网络请求并解析JSON，创建NewsBean并添加到``ArrayList<NewsBean>``中。

2. 使用OkHttp框架，[参考此博客][1]，使用最简单的GET请求即可。

3. ~~问题： ListView中图片的加载。~~

---

## Day03 数据缓存

1. 目的：将加载的数据缓存，打开应用时首先显示缓存数据，再请求网络数据。
2. 建立SQLiteDataBase：
    1. 在dao包中建立NewsOpenHelper，继承SQLiteOpenHelper，在``onCreate()``方法中建表： ``db.execSQL("create table news (_id integer primary key, title varchar(200), images varchar(200))");``
    2. 创建NewsDaoUtils类，实现``getNews()、saveNews(ArrayList<NewsBean>)、deleteNews()``方法。``getNews()``从数据库中读取，返回``ArrayList<NewsBean>``；``saveNews(ArrayList<NewsBean>)``将``ArrayList<NewsBean>``存入数据库；``deleteNews()``清空数据库。
    3. 在MainActivity中，首先判断数据库是否为空，不为空时调用``getNews()``将数据显示到ListView；然后从网络请求数据，请求成功后调用``deleteNews()``和``saveNews(ArrayList<NewsBean>)``，并将数据显示到ListView。

---

## Day04 ListView图片加载：使用Fresco库

1. 使用Fresco图片处理库 [中文文档][2]
    1. 在``onCreate()``中初始化``Fresco.initialize(this);``
        如果主布局文件中使用了Fresco，则必须在``setContentView()``之前初始化Fresco！
    2. 在布局文件中加入命名空间``xmlns:fresco="http://schemas.android.com/apk/res-auto"``
    3. 加入SimpleDraweeView，placeholderImage属性设置未加载时的显示图片：
        ```xml
        <com.facebook.drawee.view.SimpleDraweeView
        android:id="@+id/my_image_view"
        android:layout_width="130dp"
        android:layout_height="130dp"
        fresco:placeholderImage="@drawable/my_drawable"
         />
        
        ```
    4. 在Adapter类的getView方法中加载图片：
    
        ```java
        Uri uri = Uri.parse(newsBean.images);
        SimpleDraweeView draweeView = (SimpleDraweeView) view.findViewById(R.id.my_image_view);
        draweeView.setImageURI(uri);
        ```
2. 查错笔记：Fresco加载不出图片
    - 查看文档，Fresco不支持``wrap_content``，布局文件中改为固定大小。图片仍然未显示。
    - 将``Uri uri = Uri.parse(newsBean.images);``中的“newsBean.images"替换为固定uri地址，图片成功显示。说明Fresco本身没问题。
    - 配置Fresco日志输出：
    
        ```java
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(context)
           // other setters
           .setRequestListeners(requestListeners)
           .build();
        Fresco.initialize(context, config);
        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        ```
    - 日志显示``Unsupported uri scheme! Uri is: ["http://....``
    - 在图片加载前打印``newsBean.images``，显示``["http://..."]``，多了个中括号
    - 回去查看网络响应，发现返回的images属性是一个String数组，将``final String images = story.getString("images");``改为``final String images = story.getJSONArray("images").getString(0);``成功解决。
    
---

## Day05 文章内容显示

1. 为ListView设置条目点击事件，点击后获取id，拼接消息内容获取的url，通过Intent跳转到新的NewsActivity，intent中携带url字符串。
2. 为NewsActivity设置布局文件，上半部分为RelativeLayout显示图片、标题和版权信息，下半部分为WebView显示内容。
3. 通过``getIntent().getStringExtra("url");``获取intent传来的url，通过url获取网络相应，并解析JSON数据，设置给相应的控件。
4. WebView的问题及解决：
    1. 问题：将JSON数据中的body部分直接用``webView.loadData()``方法设置给WebView，设置好了编码方式为“UTF-8”，但总是显示出乱码。
    2. 解决方案：使用``webView.loadDataWithBaseURL(null, htmlData, "text/html","UTF-8", null);``成功解决乱码问题，原因不明。
    3. 问题：内容部分宽度过大，无法自适应屏幕，出现水平进度条。
    4. 解决方案：尝试了在WebSetting中设置各种属性，尝试了网上几乎所有的方案，有的在模拟器中成功解决，但在真机上均失败。注意到JSON数据中有“CSS”数据未使用，获取到之后，查阅相关文档将body部分与CSS拼接显示，成功解决：
        ```java
        String htmlData = "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssUrl + "\" />" + body;
        wvNewsContent.loadDataWithBaseURL(null, htmlData, "text/html","UTF-8", null);
        
        ```
    5. 上一步之后，WebView内部的上半部分出现一片空白，猜想是标题和图片的部分，于是将布局文件中上半部分的RelativeLayout放在WebView里面，成功解决。这样最外层的LinearLayout就没用了，直接将WebView作为根布局。
    
5. 添加下拉刷新控件[Ultra-Pull-To-Refresh][3]
    参考项目Demo，在布局中添加PtrClassicFrameLayout控件，在Activity中绑定后，设置``setPtrHandler(PtrHandler)``并在``onRefreshBegin(PtrFrameLayout)``方法中调用``getNewsForInternet()``和``ptrFrame.refreshComplete()``方法。


  [1]: http://blog.csdn.net/lmj623565791/article/details/47911083
  [2]: https://www.fresco-cn.org/docs/getting-started.html
  [3]: https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh
