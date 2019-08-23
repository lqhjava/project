package cn.bite.travel.web.servlet;

import cn.bite.travel.domain.PageBean;
import cn.bite.travel.domain.Route;
import cn.bite.travel.service.RouteService;
import cn.bite.travel.service.impl.RouteServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 旅游线路的后台业务
 */
@WebServlet("/route/*")
public class RouteServlet extends BaseServlet {
    //service层业务对象
    private RouteService routeService = new RouteServiceImpl() ;
    public void  pageQuery(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {


        //1)接收前台传递的参数数据
        String cidStr = request.getParameter("cid") ;
        String currentPageStr = request.getParameter("currentPage") ;
        String pageSizeStr = request.getParameter("pagesize") ;

        //接收旅游线路的名称rname
        String rname = request.getParameter("rname") ;

        //2)如果第一次访问，当前页码可以指定默认值/cid/pageSize
        int cid =  0 ;
        if(cidStr!=null && cidStr.length()>0){
            //cid存在
            cid = Integer.parseInt(cidStr) ;
        }

        int currentPage = 0  ;
        if(currentPageStr!=null && currentPageStr.length()>0){
            //存在,就进行转换
            currentPage = Integer.parseInt(currentPageStr) ;
        }else{
            //第一次访问,可以给默认值
            currentPage =1 ;
        }

        //每页显示条数,默认值5
        int pageSize = 0 ;
        if(pageSizeStr!=null && pageSizeStr.length()>0){
            //存在,就进行转换
            pageSize = Integer.parseInt(pageSizeStr) ;
        }else{
            //第一次访问,可以给默认值
            pageSize =5 ;
        }

        //3.调用Service完成后台封装-PageBean
       PageBean<Route> pb =  routeService.pageQuery(cid,currentPage,pageSize,rname) ; //需要传递rname
        //将PageBean对象的总记录数/总记录数/当前页码封装完成


        //4.将pageBean对象直接返回给前台页面,发送json对象

        writeValue(pb,response);



    }


    //根据旅游编号查询旅游线路信息  ----->Route---->发送Route 到前台rote_detial.html
//    public void findByRid()


}
