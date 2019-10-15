package com.liuyanzhao.sens.utils;

import com.liuyanzhao.sens.entity.Post;
import com.liuyanzhao.sens.model.dto.BackupDto;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.enums.BlogPropertiesEnum;
import com.liuyanzhao.sens.model.enums.CommonParamsEnum;
import cn.hutool.core.io.FileUtil;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Item;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;
import io.github.biezhi.ome.OhMyEmail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <pre>
 *     常用工具
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/22
 */
@Slf4j
public class SensUtils {

    /**
     * 获取备份文件信息
     *
     * @param dir dir
     * @return List
     */
    public static List<BackupDto> getBackUps(String dir) {
        String srcPathStr = System.getProperties().getProperty("user.home") + "/sens/backup/" + dir;
        File srcPath = new File(srcPathStr);
        File[] files = srcPath.listFiles();
        List<BackupDto> backupDtos = new ArrayList<>();
        BackupDto backupDto = null;
        //遍历文件
        if (null != files) {
            for (File file : files) {
                if (file.isFile()) {
                    if (StringUtils.equals(file.getName(), ".DS_Store")) {
                        continue;
                    }
                    backupDto = new BackupDto();
                    backupDto.setFileName(file.getName());
                    backupDto.setCreateAt(getCreateTime(file.getAbsolutePath()));
                    backupDto.setFileType(FileUtil.getType(file));
                    backupDto.setFileSize(parseSize(file.length()));
                    backupDto.setBackupType(dir);
                    backupDtos.add(backupDto);
                }
            }
        }
        return backupDtos;
    }

    /**
     * 转换文件大小
     *
     * @param size size
     * @return String
     */
    public static String parseSize(long size) {
        if (size < CommonParamsEnum.BYTE.getValue()) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        if (size < CommonParamsEnum.BYTE.getValue()) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < CommonParamsEnum.BYTE.getValue()) {
            size = size * 100;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "MB";
        } else {
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "." + String.valueOf((size % 100)) + "GB";
        }
    }

    /**
     * 获取文件创建时间
     *
     * @param srcPath 文件绝对路径
     * @return 时间
     */
    public static Date getCreateTime(String srcPath) {
        Path path = Paths.get(srcPath);
        BasicFileAttributeView basicview = Files.getFileAttributeView(path, BasicFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
        BasicFileAttributes attr;
        try {
            attr = basicview.readAttributes();
            Date createDate = new Date(attr.creationTime().toMillis());
            return createDate;
        } catch (Exception e) {
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.set(1970, 0, 1, 0, 0, 0);
        return cal.getTime();
    }

    /**
     * 获取文件长和宽
     *
     * @param file file
     * @return String
     */
    public static String getImageWh(File file) {
        try {
            BufferedImage image = ImageIO.read(new FileInputStream(file));
            return image.getWidth() + "x" + image.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }



    /**
     * 获取当前时间
     *
     * @return 字符串
     */
    public static String getStringDate(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(new Date());
        return dateString;
    }

    public static String getStringDate(Date date, String format) {
        Long unixTime = Long.parseLong(String.valueOf(date.getTime() / 1000));
        return Instant.ofEpochSecond(unixTime).atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 导出为文件
     *
     * @param data     内容
     * @param filePath 保存路径
     * @param fileName 文件名
     */
    public static void postToFile(String data, String filePath, String fileName) throws IOException {
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            fileWriter = new FileWriter(file.getAbsoluteFile() + "/" + fileName, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedWriter) {
                bufferedWriter.close();
            }
            if (null != fileWriter) {
                fileWriter.close();
            }
        }
    }

    /**
     * 生成rss
     *
     * @param posts posts
     * @return String
     * @throws FeedException
     */
    public static String getRss(List<Post> posts) throws FeedException {
        Channel channel = new Channel("rss_2.0");
        if (null == SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_TITLE.getProp())) {
            channel.setTitle("");
        } else {
            channel.setTitle(SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_TITLE.getProp()));
        }
        if (null == SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp())) {
            channel.setLink("");
        } else {
            channel.setLink(SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp()));
        }
        if (null == SensConst.OPTIONS.get(BlogPropertiesEnum.SEO_DESC.getProp())) {
            channel.setDescription("");
        } else {
            channel.setDescription(SensConst.OPTIONS.get(BlogPropertiesEnum.SEO_DESC.getProp()));
        }
        channel.setLanguage("zh-CN");
        List<Item> items = new ArrayList<>();
        for (Post post : posts) {
            Item item = new Item();
            item.setTitle(post.getPostTitle());
            Content content = new Content();
            String value = post.getPostContent();
            char[] xmlChar = value.toCharArray();
            for (int i = 0; i < xmlChar.length; ++i) {
                if (xmlChar[i] > 0xFFFD) {
                    xmlChar[i] = ' ';
                } else if (xmlChar[i] < 0x20 && xmlChar[i] != 't' & xmlChar[i] != 'n' & xmlChar[i] != 'r') {
                    xmlChar[i] = ' ';
                }
            }
            value = new String(xmlChar);
            content.setValue(value);
            item.setContent(content);
            item.setLink(SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp()) + "/article/" + post.getId());
            item.setPubDate(post.getCreateTime());
            items.add(item);
        }
        channel.setItems(items);
        WireFeedOutput out = new WireFeedOutput();
        return out.outputString(channel);
    }

    /**
     * 获取sitemap
     *
     * @param posts posts
     * @return String
     */
    public static String getSiteMap(List<Post> posts) {
        String head = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">";
        String urlBody = "";
        String urlItem;
        String urlPath = SensConst.OPTIONS.get(BlogPropertiesEnum.BLOG_URL.getProp()) + "/article/";
        for (Post post : posts) {
            urlItem = "<url><loc>" + urlPath + post.getId() + "</loc><lastmod>" + getStringDate(post.getCreateTime(), "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") + "</lastmod>" + "</url>";
            urlBody += urlItem;
        }
        return head + urlBody + "</urlset>";
    }

    /**
     * 配置邮件
     *
     * @param smtpHost smtpHost
     * @param userName 邮件地址
     * @param password password
     */
    public static void configMail(String smtpHost, String userName, String password) {
        Properties properties = OhMyEmail.defaultConfig(false);
        properties.setProperty("mail.smtp.host", smtpHost);
        OhMyEmail.config(properties, userName, password);
    }

    /**
     * 访问路径获取json数据
     *
     * @param enterUrl 路径
     * @return String
     */
    public static String getHttpResponse(String enterUrl) {
        BufferedReader in = null;
        StringBuffer result = null;
        try {
            URI uri = new URI(enterUrl);
            URL url = uri.toURL();
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Charset", "utf-8");
            connection.connect();
            result = new StringBuffer();
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 百度主动推送
     *
     * @param blogUrl 博客地址
     * @param token   百度推送token
     * @param urls    文章路径
     * @return String
     */
    public static String baiduPost(String blogUrl, String token, String urls) {
        String url = "http://data.zz.baidu.com/urls?site=" + blogUrl + "&token=" + token;
        String result = "";
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            //建立URL之间的连接
            URLConnection conn = new URL(url).openConnection();
            //设置通用的请求属性
            conn.setRequestProperty("Host", "data.zz.baidu.com");
            conn.setRequestProperty("User-Agent", "curl/7.12.1");
            conn.setRequestProperty("Content-Length", "83");
            conn.setRequestProperty("Content-Type", "text/plain");

            //发送POST请求必须设置如下两行
            conn.setDoInput(true);
            conn.setDoOutput(true);

            //获取conn对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            out.print(urls.trim());
            //进行输出流的缓冲
            out.flush();
            //通过BufferedReader输入流来读取Url的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.close();
                }
                if (null != in) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 判断某个后缀的文是否是图片类型
     *
     * @param suffix 后缀
     * @return 是否
     */
    public static Boolean isPicture(String suffix) {
        String allowSuffix = ".bmp .jpg .jpeg .png .gif";
        if (suffix != null) {
            if (allowSuffix.indexOf(suffix) != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获得相对时间
     * @param d
     * @return
     */
    public static String getRelativeDate(Date d) {
        long delta = ((new Date()).getTime() - d.getTime()) / 1000;
        if (delta < 0) {
            DateFormat formatter = DateFormat.getDateTimeInstance();
            return formatter.format(d);
        }
        if (delta / (60 * 60 * 24 * 365) > 0) {
            return delta / (60 * 60 * 24 * 365) + "年前";
        }
        if (delta / (60 * 60 * 24 * 30) > 0) {
            return delta / (60 * 60 * 24 * 30) + "个月前";
        }
        if (delta / (60 * 60 * 24 * 7) > 0) {
            return delta / (60 * 60 * 24 * 7) + "周前";
        }
        if (delta / (60 * 60 * 24) > 0) {
            return delta / (60 * 60 * 24) + "天前";
        }
        if (delta / (60 * 60) > 0) {
            return delta / (60 * 60) + "小时前";
        }
        if (delta / 60 > 0) {
            return delta / 60 + "分钟前";
        }
//        if (delta / 3 > 0) {
//            return delta / 3 + "秒前";
//        }
        return "刚刚";
    }

}
