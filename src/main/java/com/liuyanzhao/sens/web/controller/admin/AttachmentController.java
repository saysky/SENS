package com.liuyanzhao.sens.web.controller.admin;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.config.annotation.SystemLog;
import com.liuyanzhao.sens.entity.Attachment;
import com.liuyanzhao.sens.model.dto.AttachLocationEnum;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.dto.QueryCondition;
import com.liuyanzhao.sens.model.enums.LogTypeEnum;
import com.liuyanzhao.sens.model.enums.PostTypeEnum;
import com.liuyanzhao.sens.model.enums.ResultCodeEnum;
import com.liuyanzhao.sens.model.vo.SearchVo;
import com.liuyanzhao.sens.service.AttachmentService;
import com.liuyanzhao.sens.utils.LocaleMessageUtil;
import com.liuyanzhao.sens.utils.PageUtil;
import com.liuyanzhao.sens.utils.SensUtils;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * <pre>
 *     后台附件控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/19
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/attachment")
public class AttachmentController extends BaseController {

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private LocaleMessageUtil localeMessageUtil;

    /**
     * 获取upload的所有图片资源并渲染页面
     *
     * @param model model
     * @return 模板路径admin/admin_attachment
     */
    @GetMapping
    public String attachments(Model model,
                              @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                              @RequestParam(value = "size", defaultValue = "20") Integer pageSize,
                              @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                              @RequestParam(value = "order", defaultValue = "desc") String order,
                              @ModelAttribute SearchVo searchVo) {
        Long userId = getLoginUserId();
        Attachment condition = new Attachment();
        condition.setUserId(userId);
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Attachment> attachments = attachmentService.findAll(
                page,
                new QueryCondition<>(condition, searchVo)
        );
        model.addAttribute("attachments", attachments.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        return "admin/admin_attachment";
    }

    /**
     * 处理获取附件详情的请求
     *
     * @param model    model
     * @param attachId 附件编号
     * @return 模板路径admin/widget/_attachment-detail
     */
    @GetMapping(value = "/detail")
    public String attachmentDetail(Model model, @RequestParam("id") Long attachId) {
        Attachment attachment = attachmentService.get(attachId);
        model.addAttribute("attachment", attachment);
        if (attachment != null) {
            model.addAttribute("isPicture", SensUtils.isPicture(attachment.getAttachSuffix()));
        }
        return "admin/widget/_attachment-detail";
    }

    /**
     * 跳转选择附件页面
     *
     * @param model model
     * @return 模板路径admin/widget/_attachment-select
     */
    @GetMapping(value = "/select")
    public String selectAttachment(Model model,
                                   @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                                   @RequestParam(value = "size", defaultValue = "20") Integer pageSize,
                                   @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                                   @RequestParam(value = "order", defaultValue = "desc") String order,
                                   @RequestParam(value = "id", defaultValue = "none") String id,
                                   @RequestParam(value = "type", defaultValue = "normal") String type,
                                   @ModelAttribute SearchVo searchVo) {
        Long userId = getLoginUserId();
        Attachment condition = new Attachment();
        condition.setUserId(userId);
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Attachment> attachments = attachmentService.findAll(
                page,
                new QueryCondition<>(condition, searchVo));
        model.addAttribute("attachments", attachments.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("id", id);
        if (StringUtils.equals(type, PostTypeEnum.POST_TYPE_POST.getValue())) {
            return "admin/widget/_attachment-select-post";
        }
        return "admin/widget/_attachment-select";
    }

    /**
     * 上传附件
     *
     * @param file    file
     * @param request request
     * @return Map
     */
    @PostMapping(value = "/upload", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @SystemLog(description = "上传文件", type = LogTypeEnum.ATTACHMENT)
    public Map<String, Object> upload(@RequestParam("file") MultipartFile file,
                                      HttpServletRequest request) {
        return uploadAttachment(file, request);
    }


    public Map<String, Object> uploadAttachment(@RequestParam("file") MultipartFile file,
                                                HttpServletRequest request) {

        Long userId = getLoginUserId();
        final Map<String, Object> result = new HashMap<>(3);
        if (!file.isEmpty()) {
            try {
                final Map<String, String> resultMap = attachmentService.upload(file, request);
                if (resultMap == null || resultMap.isEmpty()) {
                    log.error("File upload failed");
                    result.put("success", ResultCodeEnum.FAIL.getCode());
                    result.put("message", localeMessageUtil.getMessage("code.admin.attachment.upload-failed"));
                    return result;
                }
                //保存在数据库
                Attachment attachment = new Attachment();
                attachment.setUserId(userId);
                attachment.setAttachName(resultMap.get("fileName"));
                attachment.setAttachPath(resultMap.get("filePath"));
                attachment.setAttachSmallPath(resultMap.get("smallPath"));
                attachment.setAttachType(file.getContentType());
                attachment.setAttachSuffix(resultMap.get("suffix"));
                attachment.setCreateTime(DateUtil.date());
                attachment.setAttachSize(resultMap.get("size"));
                attachment.setAttachWh(resultMap.get("wh"));
                attachment.setAttachLocation(resultMap.get("location"));
                attachmentService.insert(attachment);
                log.info("Upload file {} to {} successfully", resultMap.get("fileName"), resultMap.get("filePath"));
                result.put("success", ResultCodeEnum.SUCCESS.getCode());
                result.put("message", localeMessageUtil.getMessage("code.admin.attachment.upload-success"));
                result.put("url", attachment.getAttachPath());
                result.put("filename", resultMap.get("filePath"));
            } catch (Exception e) {
                log.error("Upload file failed:{}", e.getMessage());
                result.put("success", ResultCodeEnum.FAIL.getCode());
                result.put("message", localeMessageUtil.getMessage("code.admin.attachment.upload-failed"));
            }
        } else {
            log.error("File cannot be empty!");
        }
        return result;
    }

    /**
     * 移除附件的请求
     *
     * @param attachId 附件编号
     * @param request  request
     * @return JsonResult
     */
    @GetMapping(value = "/delete")
    @ResponseBody
    @SystemLog(description = "删除附件", type = LogTypeEnum.ATTACHMENT)
    public JsonResult removeAttachment(@RequestParam("id") Long attachId,
                                       HttpServletRequest request) {
        //检验权限
        Long userId = getLoginUserId();
        Attachment attachment = attachmentService.get(attachId);
        if (!Objects.equals(attachment.getUserId(), userId)) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.permission-denied"));
        }
        String attachLocation = attachment.getAttachLocation();
        String delFileName = attachment.getAttachName();
        String delSmallFileName = attachment.getAttachName();
        boolean flag = true;
        try {
            //删除数据库中的内容
            attachmentService.delete(attachId);
            if (attachLocation != null) {
                if (attachLocation.equals(AttachLocationEnum.SERVER.getValue())) {
                    //删除文件
                    String userPath = System.getProperties().getProperty("user.home") + "/sens";
                    File mediaPath = new File(userPath, attachment.getAttachPath().substring(0, attachment.getAttachPath().lastIndexOf('/')));
                    File delFile = new File(new StringBuffer(mediaPath.getAbsolutePath()).append("/").append(delFileName).toString());
                    File delSmallFile = new File(new StringBuffer(mediaPath.getAbsolutePath()).append("/").append(delSmallFileName).toString());
                    if (delFile.exists() && delFile.isFile()) {
                        flag = delFile.delete() && delSmallFile.delete();
                    }
                } else if (attachLocation.equals(AttachLocationEnum.QINIU.getValue())) {
                    //七牛删除
                    String attachPath = attachment.getAttachPath();
                    int x = attachPath.lastIndexOf("uploads");
                    if (x > -1) {
                        String key = attachPath.substring(x);
                        flag = attachmentService.deleteQiNiuAttachment(key);
                    }
                } else if (attachLocation.equals(AttachLocationEnum.UPYUN.getValue())) {
                    //又拍删除
                    String attachPath = attachment.getAttachPath();
                    String fileName = attachPath.substring(attachPath.lastIndexOf("/") + 1);
                    flag = attachmentService.deleteUpYunAttachment(fileName);
                } else {
                    //..
                }
            }
            if (flag) {
                log.info("Delete file {} successfully!", delFileName);
            } else {
                log.error("Deleting attachment {} failed!", delFileName);
                return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.delete-failed"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Deleting attachment {} failed: {}", delFileName, e.getMessage());
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), localeMessageUtil.getMessage("code.admin.common.delete-failed"));
        }
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), localeMessageUtil.getMessage("code.admin.common.delete-success"));
    }

    /**
     * froala 编辑器上传图片，返回格式
     * {"link":"http://i.froala.com/images/missing.png"}
     *
     * @param file    file
     * @param request request
     * @return Map
     */
    @PostMapping(value = "/upload/froala", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    @SystemLog(description = "富文本编辑器上传文件", type = LogTypeEnum.ATTACHMENT)
    public Map<String, Object> editorUpload(@RequestParam("file") MultipartFile file,
                                            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>(1);
        Map<String, Object> upload = uploadAttachment(file, request);
        result.put("link", upload.get("url"));
        return result;
    }

}
