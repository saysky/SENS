package com.liuyanzhao.sens.model.tag;

import com.liuyanzhao.sens.model.enums.CommentStatusEnum;
import com.liuyanzhao.sens.model.enums.MenuTypeEnum;
import com.liuyanzhao.sens.model.enums.SlideTypeEnum;
import com.liuyanzhao.sens.model.enums.WidgetTypeEnum;
import com.liuyanzhao.sens.service.*;
import freemarker.core.Environment;
import freemarker.template.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * <pre>
 *     FreeMarker自定义标签
 * </pre>
 *
 * @author : saysky
 * @date : 2018/4/26
 */
@Component
public class CommonTagDirective implements TemplateDirectiveModel {

    private static final String METHOD_KEY = "method";

    @Autowired
    private MenuService menuService;

    @Autowired
    private LinkService linkService;

    @Autowired
    private SlideService slideService;

    @Autowired
    private WidgetService widgetService;

    @Override
    public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
        DefaultObjectWrapperBuilder builder = new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25);
        if (map.containsKey(METHOD_KEY)) {
            String method = map.get(METHOD_KEY).toString();
            switch (method) {
                case "menus":
                    environment.setVariable("frontTopMenus", builder.build().wrap(menuService.findMenuTree(MenuTypeEnum.FRONT_TOP_MENU.getCode())));
                    environment.setVariable("frontMainMenus", builder.build().wrap(menuService.findMenuTree(MenuTypeEnum.FRONT_MAIN_MENU.getCode())));
                    break;
                case "slides":
                    environment.setVariable("slides", builder.build().wrap(slideService.findBySlideType(SlideTypeEnum.INDEX_SLIDE.getCode())));
                    break;
                case "links":
                    environment.setVariable("links", builder.build().wrap(linkService.findAll()));
                    break;
                case "footerWidgets":
                    environment.setVariable("footerWidgets", builder.build().wrap(widgetService.findByWidgetType(WidgetTypeEnum.FOOTER_WIDGET.getCode())));
                    break;
                default:
                    break;
            }
        }
        templateDirectiveBody.render(environment.getOut());
    }
}
