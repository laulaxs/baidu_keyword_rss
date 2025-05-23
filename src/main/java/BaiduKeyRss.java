import com.google.gson.JsonObject;
import com.rometools.rome.feed.rss.Channel;
import com.rometools.rome.feed.rss.Description;
import com.rometools.rome.feed.rss.Item;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedOutput;
import service.ListService;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * ROME 是一个专门处理 RSS/Atom 的 Java 库
 */
public class BaiduKeyRss {
    public static void main(String[] args) throws IOException, InterruptedException {
        // 创建请示task url列表
        HashMap<String, String> taskMap = new HashMap<>();
        // 分别写入对应关键字及其入口URL
        taskMap.put("疤痕","https://www.baidu.com/s?tn=news&rtt=4&bsst=1&cl=2&wd=title%3A%E7%96%A4%E7%97%95&medium=0");
//        taskMap.put("胎记","https://www.baidu.com/s?tn=news&rtt=4&bsst=1&cl=2&wd=title%3A%E8%83%8E%E8%AE%B0&medium=0");
//        taskMap.put("植发","https://www.baidu.com/s?tn=news&rtt=4&bsst=1&cl=2&wd=title%3A%E6%A4%8D%E5%8F%91&medium=0");

        // 遍历task Url，开始执行抓取主URL任务
        for (String key : taskMap.keySet()) {
            String taskUrl = taskMap.get(key);
            ListService listService = new ListService();
            // 通过Task Url获取所有列表URL
            LinkedHashSet<String> pageUrlList = listService.getPageUrlList(taskUrl);

            // 创建频道
            Channel channel = new Channel();
            channel.setFeedType("rss_2.0");
            channel.setTitle("百度关键字RSS");
            channel.setLink("https://news.baidu.com");
            channel.setDescription("-----");

            // 循环创建并添加Item
            ArrayList<Item> items = new ArrayList<>();

            // 通过pageUrl获取item结果集
            for (String pageUrl : pageUrlList) {
                LinkedHashSet<JsonObject> itemList = listService.getItemList(pageUrl);

                for (JsonObject obj : itemList) {

                    String tempTitle = String.valueOf(obj.get("title")).replace("<em>", "").replace("</em>", "");
                    tempTitle.replace("\"","");

                    String tempUrl = String.valueOf(obj.get("titleUrl"));
                    tempUrl.replace("\"","");

                    String tempTime = String.valueOf(obj.get("dispTime"));
                    tempTime.replace("\"","");

                    String tempSource = String.valueOf(obj.get("sourceName"));
                    tempSource.replace("\"","");

                    String tempDescription = String.valueOf(obj.get("summary")).replace("<em>", "").replace("</em>", "");
                    tempDescription.replace("\"","");

                    // 创建条目
                    Item item = new Item();
                    item.setTitle(tempTitle.replace("\"",""));
                    item.setLink(tempUrl.replace("\"",""));
                    item.setPubDate(new Date());
                    item.setAuthor(tempSource);

                    Description description = new Description();
                    description.setValue(tempDescription);
                    item.setDescription(description);

                    items.add(item);
                }

                // 将条目添加到频道
                channel.setItems(items);

            }

            // 生成 XML 并写入文件
            WireFeedOutput output = new WireFeedOutput();
            try {
                output.output(channel,new FileWriter("src/main/java/rss_rome.xml"));
                System.out.println("使用 ROME 生成成功！");
            } catch (FeedException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
