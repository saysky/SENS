package com.liuyanzhao.sens.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.UpYun;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.liuyanzhao.sens.entity.Attachment;
import com.liuyanzhao.sens.mapper.AttachmentMapper;
import com.liuyanzhao.sens.model.dto.AttachLocationEnum;
import com.liuyanzhao.sens.model.dto.QiNiuPutSet;
import com.liuyanzhao.sens.model.dto.SensConst;
import com.liuyanzhao.sens.model.enums.BlogPropertiesEnum;
import com.liuyanzhao.sens.service.AttachmentService;
import com.liuyanzhao.sens.utils.Md5Util;
import com.liuyanzhao.sens.utils.SensUtils;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.persistent.FileRecorder;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.upyun.UpException;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * <pre>
 *     附件业务逻辑实现类
 * </pre>
 *
 * @author : saysky
 * @date : 2018/1/10
 */
@Service
public class AttachmentServiceImpl implements AttachmentService {

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Override
    public BaseMapper<Attachment> getRepository() {
        return attachmentMapper;
    }

    @Override
    public QueryWrapper<Attachment> getQueryWrapper(Attachment attachment) {
        //对指定字段查询
        QueryWrapper<Attachment> queryWrapper = new QueryWrapper<>();
        if (attachment != null) {
            if (attachment.getUserId() != null) {
                queryWrapper.eq("user_id", attachment.getUserId());
            }
            if (StrUtil.isNotBlank(attachment.getAttachName() )) {
                queryWrapper.like("attach_name", attachment.getAttachName());
            }
            if (StrUtil.isNotBlank(attachment.getCreateBy())) {
                queryWrapper.like("create_by", attachment.getCreateBy());
            }
            if (StrUtil.isNotBlank(attachment.getAttachLocation())) {
                queryWrapper.eq("attach_location", attachment.getAttachLocation());
            }
            if (StrUtil.isNotBlank(attachment.getAttachSuffix())) {
                queryWrapper.eq("attach_suffix", attachment.getAttachSuffix());
            }
            if (attachment.getAttachOrigin() != null) {
                queryWrapper.eq("attach_origin", attachment.getAttachOrigin());
            }
        }
        return queryWrapper;
    }

    /**
     * 上传转发
     *
     * @param file    file
     * @param request request
     * @return Map
     */
    @Override
    public Map<String, String> upload(MultipartFile file, HttpServletRequest request) {
        Map<String, String> resultMap;
        String attachLoc = SensConst.OPTIONS.get(BlogPropertiesEnum.ATTACH_LOC.getProp());
        if (StrUtil.isEmpty(attachLoc)) {
            attachLoc = "server";
        }
        switch (attachLoc) {
            case "qiniu":
                resultMap = this.attachQiNiuUpload(file, request);
                break;
            case "upyun":
                resultMap = this.attachUpYunUpload(file, request);
                break;
            default:
                resultMap = this.attachUpload(file, request);
                break;
        }
        return resultMap;
    }

    /**
     * 原生服务器上传
     *
     * @param file    file
     * @param request request
     * @return Map
     */
    @Override
    public Map<String, String> attachUpload(MultipartFile file, HttpServletRequest request) {
        final Map<String, String> resultMap = new HashMap<>(6);
        try {
            //用户目录
            final StrBuilder uploadPath = new StrBuilder(System.getProperties().getProperty("user.home"));
            uploadPath.append("/sens/upload/" + DateUtil.thisYear()).append("/").append(DateUtil.thisMonth() + 1).append("/");
            final File mediaPath = new File(uploadPath.toString());
            if (!mediaPath.exists()) {
                if (!mediaPath.mkdirs()) {
                    resultMap.put("success", "0");
                    return resultMap;
                }
            }

            //不带后缀
            String nameWithOutSuffix = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.')).replaceAll(" ", "_").replaceAll(",", "");

            //文件后缀
            final String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.') + 1);

            //带后缀
            String fileName = nameWithOutSuffix + "." + fileSuffix;

            //判断文件名是否已存在
            File descFile = new File(mediaPath.getAbsoluteFile(), fileName.toString());
            int i = 1;
            while (descFile.exists()) {
                nameWithOutSuffix = nameWithOutSuffix + "(" + i + ")";
                descFile = new File(mediaPath.getAbsoluteFile(), nameWithOutSuffix + "." + fileSuffix);
                i++;
            }
            file.transferTo(descFile);

            //文件原路径
            final StrBuilder fullPath = new StrBuilder(mediaPath.getAbsolutePath());
            fullPath.append("/");
            fullPath.append(nameWithOutSuffix + "." + fileSuffix);

            //压缩文件路径
            final StrBuilder fullSmallPath = new StrBuilder(mediaPath.getAbsolutePath());
            fullSmallPath.append("/");
            fullSmallPath.append(nameWithOutSuffix);
            fullSmallPath.append("_small.");
            fullSmallPath.append(fileSuffix);

            //压缩图片
            Thumbnails.of(fullPath.toString()).size(256, 256).keepAspectRatio(false).toFile(fullSmallPath.toString());

            //映射路径
            final StrBuilder filePath = new StrBuilder("/upload/");
            filePath.append(DateUtil.thisYear());
            filePath.append("/");
            filePath.append(DateUtil.thisMonth() + 1);
            filePath.append("/");
            filePath.append(nameWithOutSuffix + "." + fileSuffix);

            //缩略图映射路径
            final StrBuilder fileSmallPath = new StrBuilder("/upload/");
            fileSmallPath.append(DateUtil.thisYear());
            fileSmallPath.append("/");
            fileSmallPath.append(DateUtil.thisMonth() + 1);
            fileSmallPath.append("/");
            fileSmallPath.append(nameWithOutSuffix);
            fileSmallPath.append("_small.");
            fileSmallPath.append(fileSuffix);

            final String size = SensUtils.parseSize(new File(fullPath.toString()).length());
            final String wh = SensUtils.getImageWh(new File(fullPath.toString()));

            resultMap.put("fileName", fileName.toString());
            resultMap.put("filePath", filePath.toString());
            resultMap.put("smallPath", fileSmallPath.toString());
            resultMap.put("suffix", fileSuffix);
            resultMap.put("size", size);
            resultMap.put("wh", wh);
            resultMap.put("location", AttachLocationEnum.SERVER.getValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 七牛云上传
     *
     * @param file    file
     * @param request request
     * @return Map
     */
    @Override
    public Map<String, String> attachQiNiuUpload(MultipartFile file, HttpServletRequest request) {
        final Map<String, String> resultMap = new HashMap<>(6);
        try {
            //华东zone0 华北 zone1 华南zone2 北美zoneNa0
            final Configuration cfg = new Configuration(Zone.zone0());
            String key = "uploads/" + DateUtil.thisYear() + "/" + (DateUtil.thisMonth() + 1) + "/" + Md5Util.getMD5Checksum(file);
            final String accessKey = SensConst.OPTIONS.get("qiniu_access_key");
            final String secretKey = SensConst.OPTIONS.get("qiniu_secret_key");
            final String domain = SensConst.OPTIONS.get("qiniu_domain");
            final String bucket = SensConst.OPTIONS.get("qiniu_bucket");
            final String smallUrl = SensConst.OPTIONS.get("qiniu_small_url");
            if (StrUtil.isEmpty(accessKey) || StrUtil.isEmpty(secretKey) || StrUtil.isEmpty(domain) || StrUtil.isEmpty(bucket)) {
                return resultMap;
            }
            final Auth auth = Auth.create(accessKey, secretKey);
            final StringMap putPolicy = new StringMap();
            putPolicy.put("returnBody", "{\"size\":$(fsize),\"w\":$(imageInfo.width),\"h\":$(imageInfo.height)}");
            final String upToken = auth.uploadToken(bucket, null, 3600, putPolicy);
            final String localTempDir = Paths.get(System.getenv("java.io.tmpdir"), bucket).toString();
            QiNiuPutSet putSet = new QiNiuPutSet();
            try {
                final FileRecorder fileRecorder = new FileRecorder(localTempDir);
                final UploadManager uploadManager = new UploadManager(cfg, fileRecorder);
                final Response response = uploadManager.put(file.getInputStream(), key, upToken, null, null);
                //解析上传成功的结果
                putSet = new Gson().fromJson(response.bodyString(), QiNiuPutSet.class);
            } catch (QiniuException e) {
                final Response r = e.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            final String filePath = domain.trim() + "/" + key;
            resultMap.put("fileName", file.getOriginalFilename());
            resultMap.put("filePath", filePath.trim());
            resultMap.put("smallPath", smallUrl == null ? filePath.trim() : (filePath + "?" + smallUrl).trim());
            resultMap.put("suffix", file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')));
            resultMap.put("size", SensUtils.parseSize(file.getSize()));
            resultMap.put("wh", putSet.getW() + "x" + putSet.getH());
            resultMap.put("location", AttachLocationEnum.QINIU.getValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 又拍云上传
     *
     * @param file    file
     * @param request request
     * @return Map
     */
    @Override
    public Map<String, String> attachUpYunUpload(MultipartFile file, HttpServletRequest request) {
        final Map<String, String> resultMap = new HashMap<>(6);
        try {
            String key = "uploads/" + DateUtil.thisYear() + "/" + (DateUtil.thisMonth() + 1) + "/" + Md5Util.getMD5Checksum(file);
            final String ossSrc = SensConst.OPTIONS.get("upyun_oss_src");
            final String ossPwd = SensConst.OPTIONS.get("upyun_oss_pwd");
            final String bucket = SensConst.OPTIONS.get("upyun_oss_bucket");
            final String domain = SensConst.OPTIONS.get("upyun_oss_domain");
            final String operator = SensConst.OPTIONS.get("upyun_oss_operator");
            final String smallUrl = SensConst.OPTIONS.get("upyun_oss_small");
            if (StrUtil.isEmpty(ossSrc) || StrUtil.isEmpty(ossPwd) || StrUtil.isEmpty(domain) || StrUtil.isEmpty(bucket) || StrUtil.isEmpty(operator)) {
                return resultMap;
            }
            final String fileName = file.getOriginalFilename();
            final String fileSuffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'));
            final UpYun upYun = new UpYun(bucket, operator, ossPwd);
            upYun.setTimeout(60);
            upYun.setApiDomain(UpYun.ED_AUTO);
            upYun.setDebug(true);
            upYun.writeFile(ossSrc + key + fileSuffix, file.getBytes(), true, null);
            final String filePath = domain.trim() + ossSrc + key + fileSuffix;
            String smallPath = filePath;
            if (smallUrl != null) {
                smallPath += smallUrl;
            }
            final BufferedImage image = ImageIO.read(file.getInputStream());
            if (image != null) {
                resultMap.put("wh", image.getWidth() + "x" + image.getHeight());
            }
            resultMap.put("fileName", fileName);
            resultMap.put("filePath", filePath.trim());
            resultMap.put("smallPath", smallPath.trim());
            resultMap.put("suffix", fileSuffix);
            resultMap.put("size", SensUtils.parseSize(file.getSize()));
            resultMap.put("location", AttachLocationEnum.UPYUN.getValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 七牛云删除附件
     *
     * @param key key
     * @return boolean
     */
    @Override
    public boolean deleteQiNiuAttachment(String key) {
        boolean flag = true;
        final Configuration cfg = new Configuration(Zone.zone0());
        final String accessKey = SensConst.OPTIONS.get("qiniu_access_key");
        final String secretKey = SensConst.OPTIONS.get("qiniu_secret_key");
        final String bucket = SensConst.OPTIONS.get("qiniu_bucket");
        if (StrUtil.isEmpty(accessKey) || StrUtil.isEmpty(secretKey) || StrUtil.isEmpty(bucket)) {
            return false;
        }
        final Auth auth = Auth.create(accessKey, secretKey);
        final BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            System.err.println(ex.code());
            System.err.println(ex.response.toString());
            flag = false;
        }
        return flag;
    }

    /**
     * 又拍云删除附件
     *
     * @param fileName fileName
     * @return boolean
     */
    @Override
    public boolean deleteUpYunAttachment(String fileName) {
        boolean flag = true;
        final String ossSrc = SensConst.OPTIONS.get("upyun_oss_src");
        final String ossPwd = SensConst.OPTIONS.get("upyun_oss_pwd");
        final String bucket = SensConst.OPTIONS.get("upyun_oss_bucket");
        final String operator = SensConst.OPTIONS.get("upyun_oss_operator");
        if (StrUtil.isEmpty(ossSrc) || StrUtil.isEmpty(ossPwd) || StrUtil.isEmpty(bucket) || StrUtil.isEmpty(operator)) {
            return false;
        }
        final UpYun upYun = new UpYun(bucket, operator, ossPwd);
        upYun.setApiDomain(UpYun.ED_AUTO);
        try {
            flag = upYun.deleteFile(ossSrc + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UpException e) {
            e.printStackTrace();
        }
        return flag;
    }


    @Override
    public Attachment insertOrUpdate(Attachment entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }

    @Override
    public Integer getTodayCount() {
        return attachmentMapper.getTodayCount();
    }

    @Override
    public Integer countByUserId(Long userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        return attachmentMapper.selectCount(queryWrapper);
    }

}
