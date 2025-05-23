package service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import util.UserAgentGenerator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ListService {
    // 获取随机user_agent
    public static String randomValue = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36";
    public static  HttpClient httpClient;

    /**
     * 通过Task Url获取所有列表URL
     * @param taskUrl 主任务
     * @return LinkedHashSet<String>
     * @throws IOException error
     * @throws InterruptedException error
     */
    public LinkedHashSet<String> getPageUrlList(String taskUrl) throws IOException, InterruptedException {
        // 如果存在分页标签，获取所有List Url连接,有序追加任务，如第1页，第2页
        LinkedHashSet<String> pageUrlList = new LinkedHashSet<>();

        // 创建 HttpClient 实例，设置超时时间为 7 秒
        httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(7)).build();

        // 创建HttpRequest对象，请求页面
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(taskUrl))
                .header("User-Agent", randomValue) // 设置 User-Agent
                .header("Accept", "application/json") // 设置 Accept 头
                .build();
        // 发送请求并获取响应
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        // 获取响应状态码
        int statusCode = response.statusCode();

        if (statusCode == 200){
            // 查看结果是否有分页
            String responseBody = response.body();
            // 使用 Jsoup 解析 HTML 内容
            Document document  = Jsoup.parse(responseBody);
            // 由ID选择器<div id="page">来控制页面是否有分布
            Elements elements = document.select("#page");

            // 将第1页作为任务加入抓取任务
            pageUrlList.add(taskUrl);

            // 如果有多页查询结果，获取所以listUrl连接
            if (!elements.isEmpty()){
                Elements obj = document.select("#page").select("a");
                for (Element link : obj) {
                    String href = link.attr("href");
                    // 分布将对应分页URL加入采集任务列表
                    pageUrlList.add("https://www.baidu.com" + href);
                }
            }
        }else {
            System.out.println("taskUrl对应页面HTTP状态码出错" + "," + statusCode + "," + taskUrl);
            return null;
        }
        return pageUrlList;

    }

    /**
     * 采集指定url对应item信息
     * @param pageUrl 页面url
     * @return json list
     * @throws InterruptedException error
     * @throws IOException error
     */
    public LinkedHashSet<JsonObject> getItemList(String pageUrl) throws InterruptedException, IOException {
        LinkedHashSet<JsonObject> itemHashSet = new LinkedHashSet<>();
        // 创建HttpRequest对象，请求页面
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(pageUrl))
                .header("User-Agent", randomValue) // 设置 User-Agent
                .header("Accept", "application/json") // 设置 Accept 头
                .build();
        Thread.sleep(3);

        // 发送请求并获取响应
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // 获取响应状态码
        int status = response.statusCode();
        if (status == 200){
            String responseBody = response.body();
            // 每条具体资讯都在h3标签中，但详细内容都在s-data数据包中，这里使用正则表达式提取
            // 编译正则表达式,创建 Matcher 对象
            Pattern pattern = Pattern.compile("\"title\":(.*?)-->");
            Matcher matcher = pattern.matcher(responseBody);
            while (matcher.find()){
                String group = matcher.group(1);
                String itemStr = "{\"title\":" + group;
                // 创建 Gson 实例
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(itemStr, JsonObject.class);
                itemHashSet.add(jsonObject);
            }

        }else {
            System.out.println("PageUrl页http状态码出错" + "," + status + "," + pageUrl);
        }

        return itemHashSet;
    }

}
