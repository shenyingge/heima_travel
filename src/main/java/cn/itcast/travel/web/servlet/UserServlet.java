package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.FavoriteService;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.FavoriteServiceImpl;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    //声明UserServlet业务对象
    private UserService service = new UserServiceImpl();
    private FavoriteService favoriteService = new FavoriteServiceImpl();
    //注册功能
    public void regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //验证校验
        String check = request.getParameter("check");
        //从session中获取验证码
        HttpSession session = request.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        session.removeAttribute("CHECKCODE_SERVER");//为了保证验证码只能使用一次
        if(checkcode_server == null || !check.equalsIgnoreCase(checkcode_server)){
            //验证码错误
            ResultInfo info = new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("验证码错误");
//            ObjectMapper mapper = new ObjectMapper();
//            String json = mapper.writeValueAsString(info);
            String json = writeValueAsString(info);
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(json);
            return;
        }
        //1、获取数据
        Map<String, String[]> map = request.getParameterMap();
        //2、封装对象
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3、调用service完成注册
        //UserService service = new UserServiceImpl();
        boolean flag = service.regist(user);
        //4、响应结果
        ResultInfo info = new ResultInfo();
        if(flag){
            //登录成功
            info.setFlag(true);
            System.out.printf("成功");
        }else{
            //登录失败
            info.setFlag(false);
            info.setErrorMsg("注册失败");
            System.out.printf("失败");
        }
        //将info对象序列化为json
//        ObjectMapper mapper = new ObjectMapper();
//        String json = mapper.writeValueAsString(info);
//        //将json数据写回客户端
//        //设置content-type
//        response.setContentType("application/json;charset=utf-8");
        String json = writeValueAsString(info);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);
    }
    //登录功能
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1、获取用户名和密码
        Map<String, String[]> map = request.getParameterMap();
        User user = new User();
        try {
            BeanUtils.populate(user,map);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //2、调用service方法
        //UserService service = new UserServiceImpl();
        User u = service.Login(user);
        //3、判断是否登录成功
        ResultInfo info = new ResultInfo();
        if(u == null){
            info.setFlag(false);
            info.setErrorMsg("用户名或密码错误");
        }
        if(u != null && !"Y".equals(u.getStatus())){
            info.setFlag(false);
            info.setErrorMsg("尚未激活");
        }
        if(u != null && "Y".equals(u.getStatus())){
            request.getSession().setAttribute("user",u);
            info.setFlag(true);
        }
        //4、响应数据
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=utf-8");
        mapper.writeValue(response.getOutputStream(),info);
    }
    //查询单个对象
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //从session中获取登录用户
        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");
        //将user写回客户端
//        ObjectMapper mapper = new ObjectMapper();
//        response.setContentType("application/json;charset=utf-8");
//        mapper.writeValue(response.getOutputStream(),user);
        writeValue(user,response);
    }
    //退出功能
    public void exit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1、销毁session
        request.getSession().invalidate();
        //2、跳转
        //重定向必须加虚拟目录
        response.sendRedirect(request.getContextPath()+"/login.html");
    }
    //激活功能
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1、获取激活码
        String code = request.getParameter("code");
        if(code != null){
            //2、调用service完成激活
            //UserService userService = new UserServiceImpl();
            boolean flag = service.active(code);
            //3、判断标记
            String msg = null;
            if(flag){
                //激活成功
                msg = "激活成功，请"+"<a href='login.html'>登录</a>";
            }else {
                //激活失败
                msg = "激活失败";
            }
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }

}
