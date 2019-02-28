package com.gillsoft;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gillsoft.entity.AgentCarrier;
import com.gillsoft.entity.Carrier;
import com.gillsoft.entity.Insurance;
import com.gillsoft.entity.Locality;
import com.gillsoft.entity.PathPoint;
import com.gillsoft.entity.Point;
import com.gillsoft.entity.Route;
import com.gillsoft.entity.RouteAgentBlock;
import com.gillsoft.entity.RouteBlock;
import com.gillsoft.entity.RoutePathTariff;
import com.gillsoft.entity.Tariff;
import com.gillsoft.entity.Trip;
import com.gillsoft.entity.TripPath;
import com.gillsoft.manager.ScheduleManager;
import com.gillsoft.model.Organisation;
import com.gillsoft.model.Price;
import com.gillsoft.model.Regularity;
import com.gillsoft.model.RoutePoint;
import com.gillsoft.model.ScheduleRoute;
import com.gillsoft.model.ScheduleRoutePoint;
import com.gillsoft.model.SegmentSeats;
import com.gillsoft.model.Vehicle;
import com.gillsoft.model.response.ScheduleResponse;
import com.gillsoft.util.StringUtil;

@RestController
@PropertySource("classpath:db.properties")
public class ScheduleController {
	
	public final static FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd");
	
	@Autowired
	private ScheduleManager manager;
	
	@Value("${user.default.login}")
	private String login;
	
	@Value("${user.default.password}")
	private String password;
	
	private String getLogin(String login) {
		if (login != null
				&& !login.isEmpty()) {
			return login;
		}
		return this.login;
	}
	
	private String getPassword(String password) {
		if (password != null
				&& !password.isEmpty()) {
			return password;
		}
		return this.password;
	}
	
	@GetMapping("/localities")
	public List<Locality> getLocalities() {
		return manager.getLocalities();
	}
	
	private Map<String, Locality> getMappedLocalities() {
		return manager.getLocalities().stream().collect(Collectors.toMap(locality -> String.valueOf(locality.getId()), locality -> locality));
	}
	
	@GetMapping("/points")
	public List<Point> getPoints() {
		return manager.getPoints();
	}
	
	private Map<String, Point> getMappedPoints() {
		return manager.getPoints().stream().collect(Collectors.toMap(point -> String.valueOf(point.getId()), point -> point));
	}
	
	@GetMapping("/routes")
	public List<Route> getRoutes() {
		return manager.getRoutes();
	}
	
	@GetMapping("/blocks")
	public List<RouteBlock> getRouteBlocks() {
		return manager.getRouteBlocks();
	}
	
	private Map<String, List<RouteBlock>> getMappedRouteBlocks() {
		return manager.getRouteBlocks().stream().collect(Collectors.groupingBy(v -> String.valueOf(v.getRouteId()),
				Collectors.mapping(v -> v, Collectors.toList())));
	}
	
	@GetMapping("/agent_blocks")
	public List<RouteAgentBlock> getRouteAgentBlocks(@RequestParam(required = false) String login, @RequestParam(required = false) String password) {
		return manager.getRouteAgentBlocks(getLogin(login), getPassword(password));
	}
	
	private Set<Integer> getBlockedRouteIds(String login, String password) {
		return manager.getRouteAgentBlocks(getLogin(login), getPassword(password))
				.stream().map(RouteAgentBlock::getRouteId).collect(Collectors.toSet());
	}
	
	@GetMapping("/agent_carriers")
	public List<AgentCarrier> getAgentCarriers(@RequestParam(required = false) String login, @RequestParam(required = false) String password) {
		return manager.getAgentCarriers(getLogin(login), getPassword(password));
	}
	
	private Set<String> getAgentCarrierCodes(String login, String password) {
		return manager.getAgentCarriers(getLogin(login), getPassword(password))
				.stream().map(a -> a.getCarrier().getCode()).collect(Collectors.toSet());
	}
	
	@GetMapping("/carriers")
	public List<Carrier> getCarriers() {
		return manager.getCarriers();
	}
	
	private Map<String, Carrier> getMappedCarriers() {
		return manager.getCarriers().stream().collect(Collectors.toMap(Carrier::getCode, carrier -> carrier));
	}
	
	@GetMapping("/insurances")
	public List<Insurance> getInsurances() {
		return manager.getInsurances();
	}
	
	private Map<String, Insurance> getMappedInsurances() {
		return manager.getInsurances().stream().collect(Collectors.toMap(Insurance::getCode, insurance -> insurance));
	}
	
	@GetMapping("/schedule")
	public ScheduleResponse getSchedule(@RequestParam(required = false) String login, @RequestParam(required = false) String password) {
		
		// выгружаем словари
		Map<String, Locality> localities = getMappedLocalities();
		Map<String, Point> points = getMappedPoints();
		Map<String, Carrier> carriers = getMappedCarriers();
		Map<String, Insurance> insurances = getMappedInsurances();
		Map<String, List<RouteBlock>> blocks = getMappedRouteBlocks();
		Set<Integer> routeBlockIds = getBlockedRouteIds(login, password);
		Set<String> carrierCodes = getAgentCarrierCodes(login, password);
		
		ScheduleResponse schedule = new ScheduleResponse();
		schedule.setId(UUID.randomUUID().toString());
		
		Map<String, Organisation> organisations = new HashMap<>();
		schedule.setOrganisations(organisations);
		
		Map<String, Vehicle> vehicles = new HashMap<>();
		schedule.setVehicles(vehicles);

		Map<String, com.gillsoft.model.Locality> resLocalities = new HashMap<>();
		schedule.setLocalities(resLocalities);
		
		Map<String, com.gillsoft.model.Locality> resParents = new HashMap<>();
		schedule.setParents(resParents);
		
		schedule.setRoutes(new ArrayList<>());
		List<Route> routes = getRoutes();
		for (Route route : routes) {
			
			// проверяем блокировку маршрута и доступность перевозчика
			if (routeBlockIds.contains(route.getId())
					|| (!carrierCodes.isEmpty()
							&& !carrierCodes.contains(route.getCarrierCode()))) {
				continue;
			}
			// добавляем организации
			addOrganisation(organisations, carriers, null, route.getCarrierCode());
			addOrganisation(organisations, null, insurances, route.getInsuranceCode());
			
			// добавляем транспорт
			String vehicleId = StringUtil.md5(route.getBusCode());
			Vehicle vehicle = vehicles.get(vehicleId);
			if (vehicle == null) {
				vehicle = new Vehicle();
				vehicle.setModel(route.getBusCode());
				vehicles.put(vehicleId, vehicle);
			}
			// создаем маршрут
			ScheduleRoute scheduleRoute = route.create();
			List<ScheduleRoutePoint> path = new ArrayList<>();
			
			// добавляем остановки на маршруте
			for (PathPoint pathPoint : route.getPath()) {
				path.add(pathPoint.create());
			}
			scheduleRoute.setPath(path);
			
			// делаем тарифную сетку
			boolean isPresentPrices = false;
			Tariff currTariff = getCurrTariff(route.getTariffs());
			Map<String, RoutePathTariff> tariffs = currTariff == null ? new HashMap<>(0) : currTariff
					.getGrids().iterator().next()
					.getValues().stream().collect(
							Collectors.toMap(value -> value.getRouteFromId() + ";" + value.getRouteToId(), value -> value));
			List<RouteBlock> reouteBlocks = blocks.get(scheduleRoute.getId());
			setIndexToBlocks(getPathMap(scheduleRoute.getPath()), reouteBlocks);
			for (int i = 0; i < scheduleRoute.getPath().size(); i++) {
				ScheduleRoutePoint point = (ScheduleRoutePoint) scheduleRoute.getPath().get(i);
				
				// проверяем можно ли продавать из это пункта
				if (!isDisabledArrivals(null, point.getIndex(), reouteBlocks)) {
					for (int j = i + 1; j < scheduleRoute.getPath().size(); j++) {
						ScheduleRoutePoint destination = (ScheduleRoutePoint) scheduleRoute.getPath().get(j);
						
						// прверяем можно ли продавать в этот пункт
						if (!isDisabledArrival(null, point.getIndex(), destination.getIndex(), reouteBlocks)) {
							RoutePathTariff tariff = tariffs.get(point.getId() + ";" + destination.getId());
							if (tariff != null) {
								ScheduleRoutePoint pricePoint = new ScheduleRoutePoint();
								pricePoint.setId(destination.getId());
								pricePoint.setIndex(destination.getIndex());
								Price price = new Price();
								price.setAmount(new BigDecimal(String.valueOf(tariff.getValue())));
								pricePoint.setPrice(price);
								if (point.getDestinations() == null) {
									point.setDestinations(new ArrayList<>());
								}
								point.getDestinations().add(pricePoint);
								isPresentPrices = true;
							}
						}
					}
				}
			}
			if (isPresentPrices) {
				
				
				// добавляем в расписание используемые города (parent) и остановки (locality)
				for (RoutePoint routePoint : scheduleRoute.getPath()) {
					if (routePoint.getLocality().getParent() != null
							&& !resParents.containsKey(routePoint.getLocality().getParent().getId())) {
						Locality locality = localities.get(routePoint.getLocality().getParent().getId());
						if (locality != null) {
							resParents.put(routePoint.getLocality().getParent().getId(), locality.create());
						}
					}
					if (!resLocalities.containsKey(routePoint.getLocality().getId())) {
						Point point = points.get(routePoint.getLocality().getId());
						if (point != null) {
							resLocalities.put(routePoint.getLocality().getId(), point.create());
						}
					}
				}
				schedule.getRoutes().add(scheduleRoute);
			}
		}
		return schedule;
	}
	
	private Map<String, Integer> getPathMap(List<? extends RoutePoint> path) {
		return path.stream().collect(Collectors.toMap(RoutePoint::getId, p -> ((ScheduleRoutePoint) p).getIndex()));
	}
	
	private void setIndexToBlocks(Map<String, Integer> path, List<RouteBlock> blocks) {
		if (blocks == null) {
			return;
		}
		for (RouteBlock block : blocks) {
			
			// устанавливаем индексы блокировки отправления
			if (path.containsKey(String.valueOf(block.getDepartFrom()))) {
				block.setDepartFromIndex(path.get(String.valueOf(block.getDepartFrom())));
			}
			if (path.containsKey(String.valueOf(block.getDepartTo()))) {
				block.setDepartToIndex(path.get(String.valueOf(block.getDepartTo())));
			}
			if (block.getDepartFromIndex() == null) {
				block.setDepartFromIndex(block.getDepartToIndex());
			}
			if (block.getDepartToIndex() == null) {
				block.setDepartToIndex(block.getDepartFromIndex());
			}
			// устанавливаем индексы блокировки прибытия
			if (path.containsKey(String.valueOf(block.getArriveFrom()))) {
				block.setArriveFromIndex(path.get(String.valueOf(block.getArriveFrom())));
			}
			if (path.containsKey(String.valueOf(block.getArriveTo()))) {
				block.setArriveToIndex(path.get(String.valueOf(block.getArriveTo())));
			}
			if (block.getArriveFromIndex() == null) {
				block.setArriveFromIndex(block.getArriveToIndex());
			}
			if (block.getArriveToIndex() == null) {
				block.setArriveToIndex(block.getArriveFromIndex());
			}
		}
	}
	
	private Tariff getCurrTariff(Set<Tariff> tariffs) {
		long curr = System.currentTimeMillis();
		if (tariffs.stream().anyMatch(t -> t.getStartedAt() != null && t.getEndedAt() != null)) {
			Optional<Tariff> optional = tariffs.stream().filter(t -> t.getStartedAt() != null && t.getStartedAt().getTime() <= curr
					&& t.getEndedAt() != null && t.getEndedAt().getTime() >= curr).findFirst();
			return optional.isPresent() ? optional.get() : null;
		} else {
			return tariffs.iterator().next();
		}
	}
	
	private boolean isDisabledArrival(Date date, int pointIndex, int destIndex, List<RouteBlock> blocks) {
		if (blocks == null) {
			return false;
		}
		long curr = date == null ? System.currentTimeMillis() : date.getTime();
		
		// если блокировка по отправлению пуста или ид отправления в нее попадает, то заблокировано прибытие
		return blocks.stream().anyMatch(routeBlock ->
			((routeBlock.getDepartFromIndex() == null && routeBlock.getDepartToIndex() == null)
				|| ((routeBlock.getDepartFromIndex() == null || routeBlock.getDepartFromIndex() <= pointIndex)
						&& (routeBlock.getDepartToIndex() == null || routeBlock.getDepartToIndex() >= pointIndex)))
				&& ((routeBlock.getArriveFromIndex() == null || routeBlock.getArriveFromIndex() <= destIndex)
						&& (routeBlock.getArriveToIndex() == null || routeBlock.getArriveToIndex() >= destIndex))
				&& (date == null || checkDate(curr, routeBlock)));
	}
	
	private boolean isDisabledArrivals(Date date, int pointIndex, List<RouteBlock> blocks) {
		if (blocks == null) {
			return false;
		}
		long curr = date == null ? System.currentTimeMillis() : date.getTime();
		
		// если есть блокировка по отправлению и нет блокировки по прибытию,
		// то значит запрещено продавать из пункта
		return blocks.stream().anyMatch(routeBlock ->
				((routeBlock.getDepartFromIndex() == null || routeBlock.getDepartFromIndex() <= pointIndex)
					&& (routeBlock.getDepartToIndex() == null || routeBlock.getDepartToIndex() >= pointIndex))
					&& routeBlock.getArriveFromIndex() == null
					&& routeBlock.getArriveToIndex() == null
					&& (date == null || checkDate(curr, routeBlock)));
	}
	
	private boolean checkDate(long curr, RouteBlock routeBlock) {
		return (routeBlock.getStartedAt() == null || routeBlock.getStartedAt().getTime() <= curr)
				&& (routeBlock.getEndedAt() == null || routeBlock.getEndedAt().getTime() >= curr)
				&& checkRegularity(curr, routeBlock);
	}
	
	private boolean checkRegularity(long curr, RouteBlock routeBlock) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(curr);
		int currDay = c.get(Calendar.DAY_OF_WEEK) - 1;
		Regularity reg = Regularity.valueOf(routeBlock.getRegularity().replaceAll(" ", "_").toUpperCase());
		switch (reg) {
		case EVERY_DAY:
			return true;
		case DAY_BY_DAY:
			Calendar start = Calendar.getInstance();
			start.setTime(routeBlock.getStartedAt());
			return (c.get(Calendar.DATE) - start.get(Calendar.DATE)) % 2 == 0;
		case DAYS_OF_THE_WEEK:
			return routeBlock.getRegularityDays().contains(currDay);
		case EVEN_DAY:
			return c.get(Calendar.DATE) % 2 == 0;
		case ODD_DAY:
			return c.get(Calendar.DATE) % 2 != 0;
		default:
			return true;
		}
	}
	
	private void addOrganisation(Map<String, Organisation> orgaisations, Map<String, Carrier> carriers,
			Map<String, Insurance> insurances, String code) {
		if (!orgaisations.containsKey(code)) {
			if (carriers != null) {
				Carrier carrier = carriers.get(code);
				if (carrier != null) {
					orgaisations.put(code, carrier.create());
				}
			} else {
				Insurance insurance = insurances.get(code);
				if (insurance != null) {
					orgaisations.put(code, insurance.create());
				}
			}
		}
	}
	
	@GetMapping("/trips")
	public Map<Integer, List<String>> getAvailableTrips(@RequestParam(required = false) String login,
			@RequestParam(required = false) String password) {
		Map<String, List<RouteBlock>> blocks = getMappedRouteBlocks();
		Set<Integer> routeBlockIds = getBlockedRouteIds(login, password);
		Set<String> carrierCodes = getAgentCarrierCodes(login, password);
		List<Trip> trips = manager.getAvailableTrips();
		
		// добавляем маршруты к рейсам
		Set<Integer> ids = trips.stream().map(Trip::getRouteId).collect(Collectors.toSet());
		List<PathPoint> routePaths = manager.getRoutePath(ids);
		Map<Integer, List<PathPoint>> pathMap = routePaths.stream().collect(
				Collectors.groupingBy(PathPoint::getRouteId, Collectors.toList()));
		
		// удаляем заблокированные рейсы
		for (Iterator<Trip> iterator = trips.iterator(); iterator.hasNext();) {
			Trip trip = iterator.next();
			
			// проверяем блокировку маршрута и доступность перевозчика
			if (routeBlockIds.contains(trip.getRouteId())
					|| (!carrierCodes.isEmpty()
							&& !carrierCodes.contains(trip.getCarrierCode()))) {
				iterator.remove();
			} else {
			
				// проверяем блокировку рейса
				List<RouteBlock> reouteBlocks = blocks.get(String.valueOf(trip.getRouteId()));
				setIndexToBlocks(getRoutePathMap(pathMap.get(trip.getRouteId())), reouteBlocks);
				if (isAllRouteBlocked(trip.getExecution(), pathMap.get(trip.getRouteId()), reouteBlocks)) {
					iterator.remove();
				}
			}
		}
		return trips.stream().collect(
				Collectors.groupingBy(Trip::getRouteId,
						Collectors.mapping(trip -> String.join(":", dateFormat.format(trip.getExecution()), String.valueOf(trip.getId())), Collectors.toList())));
	}
	
	private Map<String, Integer> getRoutePathMap(List<PathPoint> path) {
		return path.stream().collect(Collectors.toMap(p -> String.valueOf(p.getId()), p -> (int) p.getIndex()));
	}
	
	private boolean isAllRouteBlocked(Date date, List<PathPoint> routePath, List<RouteBlock> reouteBlocks) {
		Collections.sort(routePath, (r1, r2) -> r1.getIndex() > r2.getIndex() ? 1 : -1);
		for (int i = 0; i < routePath.size(); i++) {
			
			// проверяем сдвиг по маршруту
			Date calculated = null;
			if (date != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				c.add(Calendar.DATE, routePath.get(i).getDepartDay());
				calculated = c.getTime();
			}
			if (!isDisabledArrivals(calculated, routePath.get(i).getIndex(), reouteBlocks)) {
				for (int j = i + 1; j < routePath.size(); j++) {
					if (!isDisabledArrival(calculated, routePath.get(i).getIndex(), routePath.get(j).getIndex(), reouteBlocks)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	@GetMapping("/seats/{date}")
	public Map<Integer, List<SegmentSeats>> getSeats(@Validated @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		Map<String, List<RouteBlock>> blocks = getMappedRouteBlocks();
		Set<Integer> routeBlockIds = getBlockedRouteIds(login, password);
		Set<String> carrierCodes = getAgentCarrierCodes(login, password);
		List<Trip> trips = manager.getPath(date);
		Map<Integer, List<SegmentSeats>> seats = new HashMap<>();
		for (Trip trip : trips) {
			
			// проверяем блокировку маршрута и доступность перевозчика
			if (routeBlockIds.contains(trip.getRouteId())
					|| (!carrierCodes.isEmpty()
							&& !carrierCodes.contains(trip.getCarrierCode()))) {
				continue;
			}
			// проверяем блокировку рейса
			List<RouteBlock> reouteBlocks = blocks.get(String.valueOf(trip.getRouteId()));
			setIndexToBlocks(getTripPathMap(trip.getPath()), reouteBlocks);
			if (isAllBlocked(trip.getPath(), reouteBlocks)) {
				continue;
			}
			List<SegmentSeats> tripSeats = seats.get(trip.getRouteId());
			if (tripSeats == null) {
				tripSeats = new ArrayList<>();
				seats.put(trip.getRouteId(), tripSeats);
			}
			for (int i = 0; i < trip.getPath().size() - 1; i++) {
				TripPath from = trip.getPath().get(i);
				if (from.getDeparture().getTime() > date.getTime()) {
					break;
				}
				if (!isDisabledArrivals(date, from.getIndex(), reouteBlocks)) {
					Set<String> fromSeats = getEnabledSeats(from.getSeats());
					for (int j = i + 1; j < trip.getPath().size(); j++) {
						TripPath to = trip.getPath().get(j);
						if (!isDisabledArrival(date, from.getIndex(), to.getIndex(), reouteBlocks)) {
							fromSeats.retainAll(getEnabledSeats(to.getSeats()));
							Set<String> fromToSeats = new HashSet<>();
							fromToSeats.addAll(fromSeats);
							tripSeats.add(new SegmentSeats(from.createLocality(), to.createLocality(), fromToSeats.size()));
						}
					}
				}
			}
		}
		return seats;
	}
	
	private Map<String, Integer> getTripPathMap(List<TripPath> path) {
		return path.stream().collect(Collectors.toMap(p -> String.valueOf(p.getRoutePathId()), p -> (int) p.getIndex()));
	}
	
	@GetMapping("/seats_by_trip/{date}")
	public Map<Integer, Map<String, Integer>> getSeatsByTrip(@Validated @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
			@RequestParam(required = false) String login, @RequestParam(required = false) String password) {
		Map<String, List<RouteBlock>> blocks = getMappedRouteBlocks();
		Set<Integer> routeBlockIds = getBlockedRouteIds(login, password);
		Set<String> carrierCodes = getAgentCarrierCodes(login, password);
		List<Trip> trips = manager.getPathByTrip(date);
		
		// рейс -> сегмент -> количество мест
		Map<Integer, Map<String, Integer>> seats = new HashMap<>();
		for (Trip trip : trips) {
			
			// проверяем блокировку маршрута и доступность перевозчика
			if (routeBlockIds.contains(trip.getRouteId())
					|| (!carrierCodes.isEmpty()
							&& !carrierCodes.contains(trip.getCarrierCode()))) {
				continue;
			}
			// проверяем блокировку рейса
			List<RouteBlock> reouteBlocks = blocks.get(String.valueOf(trip.getRouteId()));
			setIndexToBlocks(getTripPathMap(trip.getPath()), reouteBlocks);
			if (isAllBlocked(trip.getPath(), reouteBlocks)) {
				continue;
			}
			// сегмент -> количество мест
			Map<String, Integer> tripSeats = seats.get(trip.getRouteId());
			if (tripSeats == null) {
				tripSeats = new HashMap<>();
				seats.put(trip.getRouteId(), tripSeats);
			}
			// место -> список сегментов
			Map<String, List<int[]>> seatSegments = new HashMap<>();
			for (int i = 0; i < trip.getPath().size() - 1; i++) {
				TripPath from = trip.getPath().get(i);
				Set<String> fromSeats = getEnabledSeats(from.getSeats());
				
				// создаем места с сегментами
				for (String seat : fromSeats) {
					TripPath to = trip.getPath().get(i + 1);
					List<int[]> segments = seatSegments.get(seat);
					if (segments == null) {
						segments = new ArrayList<>();
						seatSegments.put(seat, segments);
					}
					segments.add(new int[] { from.getGeoPointId(), to.getGeoPointId() });
				}
			}
			// соединяем сегменты
			Map<String, List<String>> seatStrSegments = new HashMap<>();
			for (Entry<String, List<int[]>> entry : seatSegments.entrySet()) {
				List<String> segments = new ArrayList<>();
				for (int[] segment : entry.getValue()) {
					boolean finded = false;
					for (int i = 0; i < segments.size(); i++) {
						String strSegment = segments.get(i);
						if (strSegment.startsWith(String.valueOf(segment[1]) + ":")) {
							segments.set(i, segment[0] + ":" + strSegment);
							finded = true;
							break;
						}
						if (strSegment.endsWith(":" + String.valueOf(segment[0]))) {
							segments.set(i, strSegment + ":" + segment[1]);
							finded = true;
							break;
						}
					}
					if (!finded) {
						segments.add(segment[0] + ":" + segment[1]);
					}
				}
				seatStrSegments.put(entry.getKey(), segments);
			}
			// подсчитываем места на сегментах рейса
			for (List<String> segments : seatStrSegments.values()) {
				for (String segment : segments) {
					String[] path = segment.split(":");
					String key = path[0] + ":" + path[path.length - 1];
					Integer count = tripSeats.get(key);
					if (count == null) {
						count = 0;
					}
					tripSeats.put(key, ++count);
				}
			}
		}
		seats.entrySet().removeIf(entry -> entry.getValue().isEmpty());
		return seats;
	}
	
	private boolean isAllBlocked(List<TripPath> tripPath, List<RouteBlock> reouteBlocks) {
		for (int i = 0; i < tripPath.size(); i++) {
			if (!isDisabledArrivals(tripPath.get(i).getDeparture(), tripPath.get(i).getIndex(), reouteBlocks)) {
				for (int j = i + 1; j < tripPath.size(); j++) {
					if (!isDisabledArrival(tripPath.get(i).getDeparture(), tripPath.get(i).getIndex(), tripPath.get(j).getIndex(), reouteBlocks)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private Set<String> getEnabledSeats(Map<String, String> seats) {
		return seats.entrySet().stream()
				.filter(seat -> "enable".equals(seat.getValue()))
				.map(seat -> seat.getKey()).collect(Collectors.toSet());
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(getJsonSchema(RouteBlock.class));
	}
	
	@SuppressWarnings("deprecation")
	private static String getJsonSchema(Class<?> clazz) throws IOException {
	    ObjectMapper mapper = new ObjectMapper();
	    com.fasterxml.jackson.databind.jsonschema.JsonSchema schema = mapper.generateJsonSchema(clazz);
	    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schema);
	}
	
}
