package com.gillsoft.manager;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.gillsoft.entity.AgentCarrier;
import com.gillsoft.entity.Carrier;
import com.gillsoft.entity.Insurance;
import com.gillsoft.entity.Locality;
import com.gillsoft.entity.PathPoint;
import com.gillsoft.entity.Point;
import com.gillsoft.entity.Route;
import com.gillsoft.entity.RouteAgentBlock;
import com.gillsoft.entity.RouteBlock;
import com.gillsoft.entity.Trip;

@Repository
public class ScheduleManager {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("deprecation")
	@Transactional(readOnly = true)
	public List<Locality> getLocalities() {
		return sessionFactory.getCurrentSession().createQuery(
				"from Locality as l "
				+ "join fetch l.i18n as i18n",
				Locality.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setFetchSize(1000).getResultList();
	}
	
	@SuppressWarnings("deprecation")
	@Transactional(readOnly = true)
	public List<Point> getPoints() {
		return sessionFactory.getCurrentSession().createQuery(
				"from Point as p "
				+ "join fetch p.i18n as i18n",
				Point.class).setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
				.setFetchSize(1000).getResultList();
	}
	
	@SuppressWarnings("deprecation")
	@Transactional(readOnly = true)
	public List<Route> getRoutes() {
		List<Route> routes = sessionFactory.getCurrentSession().createQuery(
				"from Route r "
				+ "join fetch r.tariffs as rt "
				+ "join fetch rt.grids as rtg "
				+ "join fetch rtg.values as rtgv "
				+ "join fetch r.path as rp "
				+ "where r.available = true "
				+ "and r.deletedAt is null "
				+ "and r.test = false "
				+ "and (r.endedAt is null or :curr <= r.endedAt) "
				+ "and rt.type = 'base' "
				+ "and rt.status = 1 "
				+ "and rt.kind = 'default' "
				+ "and (rt.endedAt is null or :curr <= rt.endedAt) "
				+ "and rtg.currency = 'UAH'",
				Route.class).setParameter("curr", new Date()).setFetchSize(1000)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).getResultList();
		return routes;
	}
	
	@Transactional(readOnly = true)
	public List<Carrier> getCarriers() {
		return sessionFactory.getCurrentSession().createQuery(
				"from Carrier c "
				+ "join fetch c.organisation as o",
				Carrier.class).getResultList();
	}
	
	@Transactional(readOnly = true)
	public List<Insurance> getInsurances() {
		return sessionFactory.getCurrentSession().createQuery(
				"from Insurance i "
				+ "join fetch i.organisation as o",
				Insurance.class).getResultList();
	}
	
	@Transactional(readOnly = true)
	public List<Trip> getAvailableTrips() {
		return sessionFactory.getCurrentSession().createQuery(
				"select t from Trip as t "
				+ "join Route as r with r.id = t.routeId "
				+ "join r.tariffs as rt "
				+ "join rt.grids as rtg "
				+ "where t.available = true "
				+ "and t.deletedAt is null "
				+ "and t.execution >= :curr "
				+ "and r.deletedAt is null "
				+ "and r.available = true "
				+ "and r.test = false "
				+ "and (r.endedAt is null or :curr <= r.endedAt) "
				+ "and rt.type = 'base' "
				+ "and rt.status = 1 "
				+ "and rt.kind = 'default' "
				+ "and (rt.endedAt is null or :curr <= rt.endedAt) "
				+ "and rtg.currency = 'UAH'",
				Trip.class).setParameter("curr", new Date()).setFetchSize(1000).getResultList();
	}
	
	@Transactional(readOnly = true)
	public List<PathPoint> getRoutePath(Set<Integer> ids) {
		return sessionFactory.getCurrentSession().createQuery(
				"select pp from PathPoint as pp "
				+ "where pp.routeId in :ids",
				PathPoint.class).setParameter("ids", ids).setFetchSize(1000).getResultList();
	}
	
	@SuppressWarnings("deprecation")
	@Transactional(readOnly = true)
	public List<Trip> getPath(Date date) {
		return sessionFactory.getCurrentSession().createQuery(
				"select t from Trip as t "
				+ "join fetch t.path as tp "
				+ "join Route as r with r.id = t.routeId "
				+ "join r.tariffs as rt "
				+ "join rt.grids as rtg "
				+ "where t.available = true "
				+ "and t.deletedAt is null "
				+ "and t.execution <= :curr "
				+ "and tp.jsonSeats is not null "
				+ "and tp.departure >= :curr "
				+ "and r.deletedAt is null "
				+ "and r.available = true "
				+ "and r.test = false "
				+ "and (r.endedAt is null or :curr <= r.endedAt) "
				+ "and rt.type = 'base' "
				+ "and rt.status = 1 "
				+ "and rt.kind = 'default' "
				+ "and (rt.endedAt is null or :curr <= rt.endedAt) "
				+ "and rtg.currency = 'UAH'",
				Trip.class).setParameter("curr", date).setFetchSize(1000)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).getResultList();
	}
	
	@SuppressWarnings("deprecation")
	@Transactional(readOnly = true)
	public List<Trip> getPathByTrip(Date date) {
		return sessionFactory.getCurrentSession().createQuery(
				"select t from Trip as t "
				+ "join fetch t.path as tp "
				+ "join Route as r with r.id = t.routeId "
				+ "join r.tariffs as rt "
				+ "join rt.grids as rtg "
				+ "where t.available = true "
				+ "and t.deletedAt is null "
				+ "and t.execution = :curr "
				+ "and tp.jsonSeats is not null "
				+ "and r.deletedAt is null "
				+ "and r.available = true "
				+ "and r.test = false "
				+ "and (r.endedAt is null or :curr <= r.endedAt) "
				+ "and rt.type = 'base' "
				+ "and rt.status = 1 "
				+ "and rt.kind = 'default' "
				+ "and (rt.endedAt is null or :curr <= rt.endedAt) "
				+ "and rtg.currency = 'UAH'",
				Trip.class).setParameter("curr", date).setFetchSize(1000)
				.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).getResultList();
	}
	
	@Transactional(readOnly = true)
	public List<RouteBlock> getRouteBlocks()  {
		return sessionFactory.getCurrentSession().createQuery(
				"from RouteBlock as r "
				+ "where r.endedAt is null or :curr <= r.endedAt",
				RouteBlock.class).setParameter("curr", new Date()).getResultList();
	}
	
	@Transactional(readOnly = true)
	public List<RouteAgentBlock> getRouteAgentBlocks(String login, String password)  {
		return sessionFactory.getCurrentSession().createQuery(
				"select rab from RouteAgentBlock as rab "
				+ "join User as u with u.agentId = rab.agentId "
				+ "where u.login = :login "
				+ "and u.password = :pass", RouteAgentBlock.class)
				.setParameter("login", login)
				.setParameter("pass", password).getResultList();
	}
	
	@Transactional(readOnly = true)
	public List<AgentCarrier> getAgentCarriers(String login, String password)  {
		return sessionFactory.getCurrentSession().createQuery(
				"select ac from AgentCarrier as ac "
				+ "join fetch ac.carrier "
				+ "join User as u with u.agentId = ac.agentId "
				+ "where u.login = :login "
				+ "and u.password = :pass", AgentCarrier.class)
				.setParameter("login", login)
				.setParameter("pass", password).getResultList();
	}

}
