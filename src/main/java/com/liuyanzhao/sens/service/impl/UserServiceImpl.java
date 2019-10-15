package com.liuyanzhao.sens.service.impl;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyanzhao.sens.common.constant.RedisKeyExpire;
import com.liuyanzhao.sens.common.constant.RedisKeys;
import com.liuyanzhao.sens.entity.Role;
import com.liuyanzhao.sens.entity.Widget;
import com.liuyanzhao.sens.exception.SensBusinessException;
import com.liuyanzhao.sens.mapper.UserMapper;
import com.liuyanzhao.sens.entity.User;
import com.liuyanzhao.sens.model.enums.RoleEnum;
import com.liuyanzhao.sens.model.enums.TrueFalseEnum;
import com.liuyanzhao.sens.service.*;
import com.liuyanzhao.sens.utils.Md5Util;
import com.liuyanzhao.sens.utils.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     用户业务逻辑实现类
 * </pre>
 *
 * @author : saysky
 * @date : 2017/11/14
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    public User findByUserName(String userName) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_name", userName);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User findByEmail(String userEmail) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_email", userEmail);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void updatePassword(Long userId, String password) {
        User user = new User();
        user.setId(userId);
        user.setUserPass(Md5Util.toMd5(password, "sens", 10));
        user.setUserPass(new Md5Hash(password, "sens", 10).toString());
        userMapper.updateById(user);
        redisUtil.del(RedisKeys.USER + userId);

    }

    @Override
    public Page<User> findByRoleAndCondition(String roleName, User condition, Page<User> page) {
        Role role = roleService.findByRoleName(roleName);
        List<User> users;
        if (role != null && !Objects.equals(roleName, RoleEnum.NONE.getValue())) {
            users = userMapper.findByRoleIdAndCondition(role.getId(), condition, page);
        } else {
            users = userMapper.findByWithoutRole(page);
        }
        return page.setRecords(users);
    }


    @Override
    public User findByUserIdAndUserPass(Long userId, String userPass) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("user_pass", userPass);
        return userMapper.selectOne(queryWrapper);
    }


    /**
     * 修改禁用状态
     *
     * @param enable enable
     */
    @Override
    public void updateUserLoginEnable(User user, String enable) {
        //如果是修改为正常, 重置错误次数
        if (Objects.equals(TrueFalseEnum.TRUE.getValue(), enable)) {
            user.setLoginError(0);
        }
        user.setLoginEnable(enable);
        user.setLoginLast(new Date());
        userMapper.updateById(user);
        redisUtil.del(RedisKeys.USER + user.getId());
    }


    /**
     * 增加登录错误次数
     *
     * @return 登录错误次数
     */
    @Override
    public Integer updateUserLoginError(User user) {
        user.setLoginError((user.getLoginError() == null ? 0 : user.getLoginError()) + 1);
        userMapper.updateById(user);
        redisUtil.del(RedisKeys.USER + user.getId());
        return user.getLoginError();
    }

    /**
     * 修改用户的状态为正常
     *
     * @return User
     */
    @Override
    public User updateUserLoginNormal(User user) {
        user.setLoginEnable(TrueFalseEnum.TRUE.getValue());
        user.setLoginError(0);
        user.setLoginLast(new Date());
        userMapper.updateById(user);
        redisUtil.del(RedisKeys.USER + user.getId());
        return user;
    }

    @Override
    public Integer getTodayCount() {
        return userMapper.getTodayCount();
    }

    @Override
    public List<User> getUserPostRanking(Integer limit) {
        return userMapper.getUserPostRanking(limit);
    }

    @Override
    public List<User> getLatestRegisterUser(Integer limit) {
        return userMapper.getLatestUser(limit);
    }


    @Override
    public BaseMapper<User> getRepository() {
        return userMapper;
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(User user) {
        //对指定字段查询
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (user != null) {
            if (StrUtil.isNotBlank(user.getUserName())) {
                queryWrapper.eq("user_name", user.getUserName());
            }
            if (StrUtil.isNotBlank(user.getUserEmail())) {
                queryWrapper.eq("user_email", user.getUserEmail());
            }
        }
        return queryWrapper;
    }

    @Override
    public User insert(User user) {
        //1.验证表单数据是否合法
        basicUserCheck(user);
        //2.验证用户名和邮箱是否存在
        checkUserNameAndUserName(user);
        String userPass = Md5Util.toMd5(user.getUserPass(), "sens", 10);
        user.setUserPass(userPass);
        userMapper.insert(user);
        return user;
    }

    @Override
    public User update(User user) {
        //1.验证表单数据是否合法
        basicUserCheck(user);
        //2.验证用户名和邮箱是否存在
        checkUserNameAndUserName(user);
        userMapper.updateById(user);
        redisUtil.del(RedisKeys.USER + user.getId());
        return user;
    }


    private void basicUserCheck(User user) {
        if (user.getUserName() == null || user.getUserEmail() == null || user.getUserDisplayName() == null) {
            throw new SensBusinessException("请输入完整信息!");
        }
        String userName = user.getUserName();
        userName = userName.trim().replaceAll(" ", "-");
        if (userName.length() < 4 || userName.length() > 20) {
            throw new SensBusinessException("用户名长度为4-20位!");
        }
        if (Strings.isNotEmpty(user.getUserPass())) {
            if (user.getUserPass().length() < 6 || user.getUserPass().length() > 20) {
                throw new SensBusinessException("用户密码为6-20位!");
            }
        }
        if (!Validator.isEmail(user.getUserEmail())) {
            throw new SensBusinessException("电子邮箱格式不合法!");
        }
        if (user.getUserDisplayName().length() < 1 || user.getUserDisplayName().length() > 20) {
            throw new SensBusinessException("昵称为2-20位");
        }
    }

    private void checkUserNameAndUserName(User user) {
        //验证用户名和邮箱是否存在
        if (user.getUserName() != null) {
            User nameCheck = findByUserName(user.getUserName());
            Boolean isExist = (user.getId() == null && nameCheck != null) ||
                    (user.getId() != null && nameCheck != null && !Objects.equals(nameCheck.getId(), user.getId()));
            if (isExist) {
                throw new SensBusinessException("用户名已经存在");
            }
        }
        if (user.getUserEmail() != null) {
            User emailCheck = findByEmail(user.getUserEmail());
            Boolean isExist = (user.getId() == null && emailCheck != null) ||
                    (user.getId() != null && emailCheck != null && !Objects.equals(emailCheck.getId(), user.getId()));
            if (isExist) {
                throw new SensBusinessException("电子邮箱已经存在");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId) {
        //删除用户
        User user = get(userId);
        if (user != null) {
            //1.修改用户状态为已删除
            userMapper.deleteById(userId);
            //2.修改用户和角色关联
            roleService.deleteByUserId(userId);
            //3.文章删除
            postService.deleteByUserId(userId);
            //4.评论删除
            commentService.deleteByUserId(userId);
            commentService.deleteByAcceptUserId(userId);
            //5.分类和标签
            categoryService.deleteByUserId(userId);
            tagService.deleteByUserId(userId);
        }
    }

    @Override
    public User insertOrUpdate(User entity) {
        if (entity.getId() == null) {
            insert(entity);
        } else {
            update(entity);
        }
        return entity;
    }

    @Override
    public User get(Long id) {
        String value = redisUtil.get(RedisKeys.USER + id);
        // 先从缓存取，缓存没有从数据库取
        if (StringUtils.isNotEmpty(value)) {
            return JSON.parseObject(value, User.class);
        }
        User user = userMapper.selectById(id);
        if(user != null) {
            redisUtil.set(RedisKeys.USER + id, JSON.toJSONString(user), RedisKeyExpire.USER);
        }
        return user;
    }
}
