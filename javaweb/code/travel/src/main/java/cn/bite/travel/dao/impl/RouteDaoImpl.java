package cn.bite.travel.dao.impl;

import cn.bite.travel.dao.RouteDao;
import cn.bite.travel.domain.Route;
import cn.bite.travel.util.JDBCUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 旅游线路商品的dao的实现层
 */
public class RouteDaoImpl implements RouteDao {
    //声明查询模板对象
    private JdbcTemplate template = new JdbcTemplate(JDBCUtils.getDataSource()) ;
    @Override
    public int findTotalPage(int cid,String rname) {


        /**
         * 通过分类id查询总记录数
         * 或者是通过线路名称模糊查询
         * @param cid
         * @return
         */
        //准备sql
//        String sql = "select  count(*) from tab_route where cid = ?" ;

        // 定义sql模板
        String sql = "select count(*) from tab_route where 1=1 " ;
        //拼接sql

        //创建条件List集合
        List params = new ArrayList() ;
        //创建StringBuffer对象
        StringBuffer sb = new StringBuffer(sql) ;
        //判断并且给参数赋值
        if(cid != 0){
            sb.append(" and cid = ? ") ;
            params.add(cid) ;
        }
        if(rname !=null && rname.length()>0){
            sb.append(" and rname like ? ") ;
            params.add("%"+rname+"%") ;
        }
        //将StringBuffer转换成String
        sb.toString() ;


//        return template.queryForObject(sql,Integer.class,cid) ;
        return template.queryForObject(sql,Integer.class,params.toArray()) ;
    }

    /**
     * 查询当前页面数据集合,通过分页查询
     * @param cid
     * @param start
     * @param pageSize
     * @return
     */
    @Override
    public List<Route> findByPage(int cid, int start, int pageSize,String rname) {
        //准备sql
//        String sql = "SELECT * FROM tab_route where  cid = ? limit ?,?" ;

        // 定义sql模板
        String sql = "select *  from tab_route where 1=1 " ;
        //拼接sql

        //创建条件List集合
        List params = new ArrayList() ;
        //创建StringBuffer对象
        StringBuffer sb = new StringBuffer(sql) ;
        //判断并且给参数赋值
        if(cid != 0){
            sb.append("and cid = ? ") ;
            params.add(cid) ;
        }
        if(rname !=null && rname.length()>0){
            sb.append("and rname like ? ") ;
            params.add("%"+rname+"%") ;
        }

        sb.append(" and limit ? ,? ") ;
        //将StringBuffer转换成String
        sb.toString() ;
        System.out.println(sql);
        params.add(start) ;
        params.add(pageSize) ;



        //查询
//        return template.query(sql,new BeanPropertyRowMapper<Route>(Route.class),cid,start,pageSize) ;
        return template.query(sql,new BeanPropertyRowMapper<Route>(Route.class),params.toArray()) ;
    }
}
