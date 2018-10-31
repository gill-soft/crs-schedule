package com.gillsoft;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
import com.gillsoft.entity.RoutePathTariff;
import com.gillsoft.entity.Trip;
import com.gillsoft.entity.TripPath;
import com.gillsoft.manager.ScheduleManager;
import com.gillsoft.model.Organisation;
import com.gillsoft.model.Price;
import com.gillsoft.model.ScheduleRoute;
import com.gillsoft.model.ScheduleRoutePoint;
import com.gillsoft.model.SegmentSeats;
import com.gillsoft.model.Vehicle;
import com.gillsoft.model.response.ScheduleResponse;
import com.gillsoft.util.StringUtil;

@RestController
public class ScheduleController {
	
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
		List<Route> routes = manager.getRoutes();
		List<Route> tariffs = manager.getTariffs();
		Map<Integer, Route> routeMap = routes.stream().collect(Collectors.toMap(Route::getId, route -> route));
		for (Iterator<Route> iterator = tariffs.iterator(); iterator.hasNext();) {
			Route route = iterator.next();
			Route path = routeMap.get(route.getId());
			if (path == null) {
				iterator.remove();
			} else if (path.getPath().size() <= 1) {
				iterator.remove();
			} else {
				route.setPath(path.getPath());
			}
		}
		return tariffs;
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
			schedule.getRoutes().add(scheduleRoute);
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
			Map<String, RoutePathTariff> tariffs = route.getTariffs().iterator().next()
					.getGrids().iterator().next()
					.getValues().stream().collect(
							Collectors.toMap(value -> value.getRouteFromId() + ";" + value.getRouteToId(), value -> value));
			for (int i = 0; i < scheduleRoute.getPath().size(); i++) {
				ScheduleRoutePoint point = (ScheduleRoutePoint) scheduleRoute.getPath().get(i);
				for (int j = i + 1; j < scheduleRoute.getPath().size(); j++) {
					ScheduleRoutePoint destination = (ScheduleRoutePoint) scheduleRoute.getPath().get(j);
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
					}
				}
			}
		}
		return schedule;
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
	public Map<Integer, List<Date>> getAvailableTrips() {
		return manager.getAvailableTrips().stream().collect(
				Collectors.groupingBy(Trip::getRouteId,
						Collectors.mapping(Trip::getExecution, Collectors.toList())));
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
	public Map<String, Integer> getSeatsByTrip(@Validated @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
		List<Trip> trips = manager.getPathByTrip(date);
		Map<String, Integer> seats = new HashMap<>();
		for (Trip trip : trips) {
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
					
					String key = from.getGeoPointId() + ":" + to.getGeoPointId();
					Integer count = seats.get(key);
					if (count == null) {
						count = 0;
					}
					count += fromToSeats.size();
					seats.put(key, count);
				}
			}
		}
		return seats;
	}
	
	private Set<String> getEnabledSeats(Map<String, String> seats) {
		return seats.entrySet().stream()
				.filter(seat -> "enable".equals(seat.getValue()))
				.map(seat -> seat.getKey()).collect(Collectors.toSet());
	}
	
}
