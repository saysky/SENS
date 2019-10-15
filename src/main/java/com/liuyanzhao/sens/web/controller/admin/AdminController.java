package com.liuyanzhao.sens.web.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.entity.*;
import com.liuyanzhao.sens.model.dto.JsonResult;
import com.liuyanzhao.sens.model.enums.*;
import com.liuyanzhao.sens.service.*;
import com.liuyanzhao.sens.utils.PageUtil;
import com.liuyanzhao.sens.web.controller.common.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.management.*;
import java.util.*;

/**
 * <pre>
 *     后台首页控制器
 * </pre>
 *
 * @author : saysky
 * @date : 2017/12/5
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private LogService logService;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PermissionService permissionService;

    /**
     * 请求后台页面
     *
     * @param model model
     * @return 模板路径admin/admin_index
     */
    @GetMapping
    public String index(Model model) {
        Boolean isAdmin = loginUserIsAdmin();
        User user = getLoginUser();

        if (isAdmin) {
            //用户总数、文章总数、评论总数、附件总数
            model.addAttribute("userTotalCount", userService.getTotalCount());
            model.addAttribute("postTotalCount", postService.getTotalCount());
            model.addAttribute("commentTotalCount", commentService.getTotalCount());
            model.addAttribute("attachmentTotalCount", attachmentService.getTotalCount());
        } else {
            //文章数、评论数、附件数、注册时间
            model.addAttribute("userTotalCount", userService.getTotalCount());
            model.addAttribute("postTotalCount", postService.countByUserId(user.getId()));
            model.addAttribute("commentTotalCount", commentService.countByAcceptUser(user.getId()));
            model.addAttribute("attachmentTotalCount", attachmentService.countByUserId(user.getId()));
        }

        model.addAttribute("createTime", user.getCreateTime());

        //用户排行榜、最近注册用户，最新登录\注册\忘记密码日志
        model.addAttribute("userRanking", userService.getUserPostRanking(5));
        model.addAttribute("newUsers", userService.getLatestRegisterUser(8));
        List<String> logTypes = new ArrayList<>();
        logTypes.add(LogTypeEnum.LOGIN.getValue());
        logTypes.add(LogTypeEnum.REGISTER.getValue());
        logTypes.add(LogTypeEnum.FORGET.getValue());
        model.addAttribute("logs", logService.findLatestLogByLogTypes(logTypes, 10));
        return "admin/admin_index";
    }


    /**
     * 查看所有日志
     *
     * @param model      model model
     * @param pageNumber page 当前页码
     * @param pageSize   size 每页条数
     * @return 模板路径admin/widget/_logs-all
     */
    @GetMapping(value = "/logs")
    public String logs(Model model,
                       @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                       @RequestParam(value = "size", defaultValue = "20") Integer pageSize,
                       @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                       @RequestParam(value = "order", defaultValue = "desc") String order) {
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Log> logs = logService.findAll(page);
        model.addAttribute("logs", logs.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        return "admin/widget/_logs-all";
    }

    /**
     * 清除所有日志
     *
     * @return 重定向到/admin
     */
    @GetMapping(value = "/logs/clear")
    public String logsClear() {
        logService.removeAllLog();
        return "redirect:/admin";
    }

    /**
     * 不可描述的页面
     *
     * @return 模板路径admin/admin_sens
     */
    @GetMapping(value = "/sens")
    public String sens() {
        return "admin/admin_sens";
    }


    /**
     * 获得当前用户的菜单
     *
     * @return
     */
    @GetMapping(value = "/currentMenus")
    @ResponseBody
    public JsonResult getMenu() {
        Long userId = getLoginUserId();
        List<Permission> permissions = permissionService.findPermissionTreeByUserIdAndResourceType(userId, "menu");
        return new JsonResult(ResultCodeEnum.SUCCESS.getCode(), "", permissions);
    }

    /**
     * 查看memory
     *
     * @return
     */
    @GetMapping("/memory")
    @ResponseBody
    public String memory() {

        StringBuilder sb = new StringBuilder();

        MemoryMXBean memorymbean = ManagementFactory.getMemoryMXBean();
        MemoryUsage usage = memorymbean.getHeapMemoryUsage();
        sb.append("INIT HEAP: " + usage.getInit() / 1024 / 2024 + "MB\n");
        sb.append("MAX HEAP: " + usage.getMax() / 1024 / 2024 + "MB\n");
        sb.append("USE HEAP: " + usage.getUsed() / 1024 / 2024 + "MB\n");
        sb.append("\nFull Information:");
        sb.append("Heap Memory Usage: "
                + memorymbean.getHeapMemoryUsage() + "\n");
        sb.append("Non-Heap Memory Usage: "
                + memorymbean.getNonHeapMemoryUsage() + "\n");

        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        sb.append("===================java options=============== \n");
        sb.append(inputArguments + "\n");


        sb.append("=======================通过java来获取相关系统状态============================ \n");
        //Java 虚拟机中的内存总量,以字节为单位
        int i = (int) Runtime.getRuntime().totalMemory() / 1024 / 1024;
        sb.append("总的内存量：" + i + "MB\n");
        //Java 虚拟机中的空闲内存量
        int j = (int) Runtime.getRuntime().freeMemory() / 1024 / 1024;
        sb.append("空闲内存量：" + j + "MB\n");
        sb.append("最大内存量： " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + "MB\n");

        sb.append("=======================OperatingSystemMXBean============================ \n");
        OperatingSystemMXBean osm = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        //获取操作系统相关信息
        sb.append("osm.getArch() " + osm.getArch() + "\n");
        sb.append("osm.getAvailableProcessors() " + osm.getAvailableProcessors() + "\n");
        sb.append("osm.getName() " + osm.getName() + "\n");
        sb.append("osm.getVersion() " + osm.getVersion() + "\n");
        //获取整个虚拟机内存使用情况
        sb.append("=======================MemoryMXBean============================ \n");
        MemoryMXBean mm = (MemoryMXBean) ManagementFactory.getMemoryMXBean();
        sb.append("getHeapMemoryUsage " + mm.getHeapMemoryUsage() + "\n");
        sb.append("getNonHeapMemoryUsage " + mm.getNonHeapMemoryUsage() + "\n");
        //获取各个线程的各种状态，CPU 占用情况，以及整个系统中的线程状况
        sb.append("=======================ThreadMXBean============================ \n");
        ThreadMXBean tm = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        sb.append("getThreadCount " + tm.getThreadCount() + "\n");
        sb.append("getPeakThreadCount " + tm.getPeakThreadCount() + "\n");
        sb.append("getCurrentThreadCpuTime " + tm.getCurrentThreadCpuTime() + "\n");
        sb.append("getDaemonThreadCount " + tm.getDaemonThreadCount() + "\n");
        sb.append("getCurrentThreadUserTime " + tm.getCurrentThreadUserTime() + "\n");

        //当前编译器情况
        sb.append("=======================CompilationMXBean============================ \n");
        CompilationMXBean gm = (CompilationMXBean) ManagementFactory.getCompilationMXBean();
        sb.append("getName " + gm.getName() + "\n");
        sb.append("getTotalCompilationTime " + gm.getTotalCompilationTime() + "\n");

        //获取多个内存池的使用情况
        sb.append("=======================MemoryPoolMXBean============================ \n");
        List<MemoryPoolMXBean> mpmList = ManagementFactory.getMemoryPoolMXBeans();
        for (MemoryPoolMXBean mpm : mpmList) {
            sb.append("getUsage " + mpm.getUsage() + "\n");
            sb.append("getMemoryManagerNames " + mpm.getMemoryManagerNames().toString() + "\n");
        }
        //获取GC的次数以及花费时间之类的信息
        sb.append("=======================MemoryPoolMXBean============================ \n");
        List<GarbageCollectorMXBean> gcmList = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcm : gcmList) {
            sb.append(gcm.getName() + "\n");
        }
        //获取运行时信息
        sb.append("=======================RuntimeMXBean============================ \n");
        RuntimeMXBean rmb = (RuntimeMXBean) ManagementFactory.getRuntimeMXBean();
        sb.append("getClassPath " + rmb.getClassPath() + "\n");
        sb.append("getLibraryPath " + rmb.getLibraryPath() + "\n");
        sb.append("getVmVersion " + rmb.getVmVersion() + "\n");

        return sb.toString();
    }
}
