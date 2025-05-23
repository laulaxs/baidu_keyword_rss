package util;

import java.util.Random;

public class UserAgentGenerator {
    public static String getRandomUserAgent(){
        // 浏览器类型
        String[] browsers = {"Mozilla/5.0"};
        // 操作系统及版本
        String[] os = {"Windows NT 10.0; Win64; x64", "Macintosh; Intel Mac OS X 10_15_7", "X11; Linux x86_64"};
        // 浏览器版本
        String[] browserVersions = {"AppleWebKit/537.36 (KHTML, like Gecko)", "Chrome/91.0.4472.164", "Safari/537.36"};
        // 设备类型
        String[] devices = {"windows", "mac", "linux"};

        Random random = new Random();

        // 随机选择浏览器类型
        String browser = browsers[random.nextInt(browsers.length)];
        // 随机选择操作系统及版本
        String osVersion = os[random.nextInt(os.length)];
        // 随机选择浏览器版本
        String browserVersion = browserVersions[random.nextInt(browserVersions.length)];
        // 随机选择设备类型
        String device = devices[random.nextInt(devices.length)];

        // 构建 User-Agent 字符串
        return browser + " (" + osVersion + "; " + device + ") " + browserVersion;
    }
}
