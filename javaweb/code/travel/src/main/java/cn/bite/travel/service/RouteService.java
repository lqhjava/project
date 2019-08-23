package cn.bite.travel.service;

import cn.bite.travel.domain.PageBean;
import cn.bite.travel.domain.Route;

/**
 * 旅游线路商品的业务接口层
 */
public interface RouteService {
    PageBean<Route> pageQuery(int cid, int currentPage, int pageSize,String rname);
}
