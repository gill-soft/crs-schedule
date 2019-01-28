package com.gillsoft;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.logging.log4j.core.util.datetime.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.gillsoft.entity.Carrier;
import com.gillsoft.entity.Insurance;
import com.gillsoft.entity.Locality;
import com.gillsoft.entity.PathPoint;
import com.gillsoft.entity.Point;
import com.gillsoft.entity.Route;
import com.gillsoft.entity.RouteBlock;
import com.gillsoft.entity.RoutePathTariff;
import com.gillsoft.entity.Tariff;
import com.gillsoft.entity.Trip;
import com.gillsoft.entity.TripPath;
import com.gillsoft.manager.ScheduleManager;
import com.gillsoft.model.Organisation;
import com.gillsoft.model.Price;
import com.gillsoft.model.RoutePoint;
import com.gillsoft.model.ScheduleRoute;
import com.gillsoft.model.ScheduleRoutePoint;
import com.gillsoft.model.SegmentSeats;
import com.gillsoft.model.Vehicle;
import com.gillsoft.model.response.ScheduleResponse;
import com.gillsoft.util.StringUtil;

@RestController
public class ScheduleController {
	
	public final static FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd");
	
	@Autowired
	private ScheduleManager manager;

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
	public ScheduleResponse getSchedule() {
		
		// выгружаем словари
		Map<String, Locality> localities = getMappedLocalities();
		Map<String, Point> points = getMappedPoints();
		Map<String, Carrier> carriers = getMappedCarriers();
		Map<String, Insurance> insurances = getMappedInsurances();
		Map<String, List<RouteBlock>> blocks = getMappedRouteBlocks();
		
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
			
			// добавляем остановки на саршруте
			for (PathPoint pathPoint : route.getPath()) {
				ScheduleRoutePoint routePoint = pathPoint.create();
				
				// добавляем в расписание используемые города (parent) и остановки (locality)
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
				path.add(routePoint);
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
			setIndexToBlocks(scheduleRoute.getPath(), reouteBlocks);
			for (int i = 0; i < scheduleRoute.getPath().size(); i++) {
				ScheduleRoutePoint point = (ScheduleRoutePoint) scheduleRoute.getPath().get(i);
				
				// проверяем можно ли продавать из это пункта
				if (!isDisabledArrivals(point, reouteBlocks)) {
					for (int j = i + 1; j < scheduleRoute.getPath().size(); j++) {
						ScheduleRoutePoint destination = (ScheduleRoutePoint) scheduleRoute.getPath().get(j);
						
						// прверяем можно ли продавать в этот пункт
						if (!isDisabledArrival(point, destination, reouteBlocks)) {
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
				schedule.getRoutes().add(scheduleRoute);
			}
		}
		return schedule;
	}
	
	private void setIndexToBlocks(List<? extends RoutePoint> path, List<RouteBlock> blocks) {
		if (blocks == null) {
			return;
		}
		for (RouteBlock block : blocks) {
			Optional<? extends RoutePoint> point = path.stream().filter(
					p -> Objects.equals(String.valueOf(block.getDepartFrom()), p.getId())).findFirst();
			if (point.isPresent()) {
				block.setDepartFromIndex(((ScheduleRoutePoint) point.get()).getIndex());
			}
			point = path.stream().filter(
					p -> Objects.equals(String.valueOf(block.getDepartTo()), p.getId())).findFirst();
			if (point.isPresent()) {
				block.setDepartToIndex(((ScheduleRoutePoint) point.get()).getIndex());
			}
			point = path.stream().filter(
					p -> Objects.equals(String.valueOf(block.getArriveFrom()), p.getId())).findFirst();
			if (point.isPresent()) {
				block.setArriveFromIndex(((ScheduleRoutePoint) point.get()).getIndex());
			}
			point = path.stream().filter(
					p -> Objects.equals(String.valueOf(block.getArriveTo()), p.getId())).findFirst();
			if (point.isPresent()) {
				block.setArriveToIndex(((ScheduleRoutePoint) point.get()).getIndex());
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
	
	private boolean isDisabledArrival(ScheduleRoutePoint point, ScheduleRoutePoint destination, List<RouteBlock> blocks) {
		if (blocks == null) {
			return false;
		}
		long curr = System.currentTimeMillis();
		int pointIndex = point.getIndex();
		int destIndex = destination.getIndex();
		
		// если блокировка по отправлению пуста или ид отправления в нее попадает, то заблокировано прибытие
		return blocks.stream().anyMatch(routeBlock ->
			((routeBlock.getDepartFromIndex() == null && routeBlock.getDepartToIndex() == null)
				|| ((routeBlock.getDepartFromIndex() == null || routeBlock.getDepartFromIndex() <= pointIndex)
						&& (routeBlock.getDepartToIndex() == null || routeBlock.getDepartToIndex() >= pointIndex)))
				&& ((routeBlock.getArriveFromIndex() == null || routeBlock.getArriveFromIndex() <= destIndex)
						&& (routeBlock.getArriveToIndex() == null || routeBlock.getArriveToIndex() >= destIndex))
				&& checkDate(curr, routeBlock));
	}
	
	private boolean isDisabledArrivals(ScheduleRoutePoint point, List<RouteBlock> blocks) {
		if (blocks == null) {
			return false;
		}
		long curr = System.currentTimeMillis();
		int pointIndex = point.getIndex();
		
		// если есть блокировка по отправлению и нет блокировки по прибытию,
		// то значит запрещено продавать из пункта
		return blocks.stream().anyMatch(routeBlock ->
				((routeBlock.getDepartFromIndex() == null || routeBlock.getDepartFromIndex() <= pointIndex)
					&& (routeBlock.getDepartToIndex() == null || routeBlock.getDepartToIndex() >= pointIndex))
					&& routeBlock.getArriveFromIndex() == null
					&& routeBlock.getArriveToIndex() == null
					&& checkDate(curr, routeBlock));
	}
	
	private boolean checkDate(long curr, RouteBlock routeBlock) {
		return (routeBlock.getStartedAt() == null || routeBlock.getStartedAt().getTime() <= curr)
				&& (routeBlock.getEndedAt() == null || routeBlock.getEndedAt().getTime() >= curr)
				&& Objects.equals(routeBlock.getRegularity(), "every day");
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
	public Map<Integer, List<String>> getAvailableTrips() {
		return manager.getAvailableTrips().stream().collect(
				Collectors.groupingBy(Trip::getRouteId,
						Collectors.mapping(trip -> String.join(":", dateFormat.format(trip.getExecution()), String.valueOf(trip.getId())), Collectors.toList())));
	}
	
	@GetMapping("/seats/{date}")
	public Map<Integer, List<SegmentSeats>> getSeats(@Validated @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		List<Trip> trips = manager.getPath(date);
		Map<Integer, List<SegmentSeats>> seats = new HashMap<>();
		for (Trip trip : trips) {
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
				Set<String> fromSeats = getEnabledSeats(from.getSeats());
				for (int j = i + 1; j < trip.getPath().size(); j++) {
					TripPath to = trip.getPath().get(j);
					fromSeats.retainAll(getEnabledSeats(to.getSeats()));
					Set<String> fromToSeats = new HashSet<>();
					fromToSeats.addAll(fromSeats);
					tripSeats.add(new SegmentSeats(from.createLocality(), to.createLocality(), fromToSeats.size()));
				}
			}
		}
		return seats;
	}
	
	@GetMapping("/seats_by_trip/{date}")
	public Map<Integer, Map<String, Integer>> getSeatsByTrip(@Validated @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		List<Trip> trips = manager.getPathByTrip(date);
		
		// рейс -> сегмент -> количество мест
		Map<Integer, Map<String, Integer>> seats = new HashMap<>();
		for (Trip trip : trips) {
			
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
	
	private Set<String> getEnabledSeats(Map<String, String> seats) {
		return seats.entrySet().stream()
				.filter(seat -> "enable".equals(seat.getValue()))
				.map(seat -> seat.getKey()).collect(Collectors.toSet());
	}
	
}
