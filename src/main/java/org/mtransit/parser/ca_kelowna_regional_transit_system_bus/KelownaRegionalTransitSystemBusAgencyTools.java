package org.mtransit.parser.ca_kelowna_regional_transit_system_bus;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.commons.StrategicMappingCommons;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.Pair;
import org.mtransit.parser.SplitUtils;
import org.mtransit.parser.SplitUtils.RouteTripSpec;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.gtfs.data.GTripStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;
import org.mtransit.parser.mt.data.MTripStop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

// https://www.bctransit.com/open-data
// https://www.bctransit.com/data/gtfs/kelowna.zip
// https://kelowna.mapstrat.com/current/google_transit.zip
public class KelownaRegionalTransitSystemBusAgencyTools extends DefaultAgencyTools {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-kelowna-regional-transit-system-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new KelownaRegionalTransitSystemBusAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@SuppressWarnings("FieldCanBeLocal")
	private boolean isNext;

	@Override
	public void start(String[] args) {
		MTLog.log("Generating Kelowna Regional TS bus data...");
		long start = System.currentTimeMillis();
		this.isNext = "next_".equalsIgnoreCase(args[2]);
		if (isNext) {
			setupNext();
		}
		this.serviceIds = extractUsefulServiceIds(args, this, true);
		super.start(args);
		MTLog.log("Generating Kelowna Regional TS bus data... DONE in %s.", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	private void setupNext() {
	}

	@Override
	public boolean excludingAll() {
		return this.serviceIds != null && this.serviceIds.isEmpty();
	}

	private static final String INCLUDE_ONLY_SERVICE_ID_CONTAINS = null;

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		//noinspection ConstantConditions
		if (INCLUDE_ONLY_SERVICE_ID_CONTAINS != null
				&& !gCalendar.getServiceId().contains(INCLUDE_ONLY_SERVICE_ID_CONTAINS)) {
			return true;
		}
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		//noinspection ConstantConditions
		if (INCLUDE_ONLY_SERVICE_ID_CONTAINS != null
				&& !gCalendarDates.getServiceId().contains(INCLUDE_ONLY_SERVICE_ID_CONTAINS)) {
			return true;
		}
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeRoute(GRoute gRoute) {
		return super.excludeRoute(gRoute);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		//noinspection ConstantConditions
		if (INCLUDE_ONLY_SERVICE_ID_CONTAINS != null
				&& !gTrip.getServiceId().contains(INCLUDE_ONLY_SERVICE_ID_CONTAINS)) {
			return true;
		}
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public long getRouteId(GRoute gRoute) {  // used by GTFS-RT
		return super.getRouteId(gRoute); // used by GTFS-RT
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.getRouteLongName();
		if (StringUtils.isEmpty(routeLongName)) {
			routeLongName = gRoute.getRouteDesc();
		}
		if (StringUtils.isEmpty(routeLongName)) {
			MTLog.logFatal("Unexpected route long name for %s!", gRoute);
			return null;
		}
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		routeLongName = CleanUtils.cleanNumbers(routeLongName);
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR_GREEN = "34B233";// GREEN (from PDF Corporate Graphic Standards)
	@SuppressWarnings("unused")
	private static final String AGENCY_COLOR_BLUE = "002C77"; // BLUE (from PDF Corporate Graphic Standards)

	private static final String AGENCY_COLOR = AGENCY_COLOR_GREEN;

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String COLOR_3FA0D6 = "3FA0D6";
	private static final String COLOR_7B9597 = "7B9597";
	private static final String COLOR_F17C15 = "F17C15";

	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Override
	public String getRouteColor(GRoute gRoute) {
		if (StringUtils.isEmpty(gRoute.getRouteColor())) {
			int rsn = Integer.parseInt(gRoute.getRouteShortName());
			switch (rsn) {
			// @formatter:off
			case 1: return COLOR_3FA0D6;
			case 2: return COLOR_7B9597;
			case 3: return COLOR_7B9597;
			case 4: return COLOR_7B9597;
			case 5: return COLOR_3FA0D6;
			case 6: return COLOR_7B9597;
			case 7: return COLOR_3FA0D6;
			case 8: return COLOR_3FA0D6;
			case 9: return COLOR_7B9597;
			case 10: return COLOR_3FA0D6;
			case 11: return COLOR_3FA0D6;
			case 12: return COLOR_7B9597;
			case 13: return COLOR_7B9597;
			case 14: return COLOR_7B9597;
			case 15: return COLOR_7B9597;
			case 16: return COLOR_7B9597;
			case 17: return COLOR_7B9597;
			case 18: return COLOR_7B9597;
			case 19: return COLOR_7B9597;
			case 20: return COLOR_7B9597;
			case 21: return COLOR_7B9597;
			case 22: return COLOR_7B9597;
			case 23: return COLOR_3FA0D6;
			case 24: return COLOR_7B9597;
			case 25: return COLOR_7B9597;
			case 27: return COLOR_7B9597;
			case 28: return COLOR_7B9597;
			case 29: return COLOR_7B9597;
			case 32: return COLOR_7B9597;
			case 90: return COLOR_7B9597;
			case 97: return COLOR_F17C15;
			// @formatter:on
			default:
				MTLog.logFatal("Unexpected route color %s!", gRoute);
				return null;
			}
		}
		return super.getRouteColor(gRoute);
	}

	private static final String EXCH = "Exch";

	private static final String BLACK_MOUNTAIN = "Black Mtn";
	private static final String SOUTH_PANDOSY = "South Pandosy";
	private static final String MISSION_REC_EXCH = "Mission Rec " + EXCH;
	private static final String UBCO = "UBCO";
	private static final String RUTLAND = "Rutland";
	private static final String QUEENSWAY_EXCH = "Queensway " + EXCH;
	private static final String OK_COLLEGE = "OK College";
	private static final String ORCHARD_PK = "Orchard Pk";
	private static final String GLENROSA = "Glenrosa";
	private static final String LK_COUNTRY = "Lk Country";
	private static final String WESTBANK = "Westbank";
	private static final String DOWNTOWN = "Downtown";

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;

	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<>();
		map2.put(2L, new RouteTripSpec(2L, // 2 //
				StrategicMappingCommons.CLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, QUEENSWAY_EXCH, //
				StrategicMappingCommons.CLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, "Cambridge & Ellis") //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_0, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("102932"), // Cambridge at Ellis (EB) <= CONTINUE
								Stops.getALL_STOPS().get("102895"), // ++ Richter at Gaston (SB)
								Stops.getALL_STOPS().get("102859") // Queensway Exchange Bay E
						)) //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_1, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("102859"), // Queensway Exchange Bay E
								Stops.getALL_STOPS().get("102888"), // ++ Ellis at Industrial (NB)
								Stops.getALL_STOPS().get("102932") // Cambridge at Ellis (EB) => CONTINUE
						)) //
				.compileBothTripSort());
		map2.put(3L, new RouteTripSpec(3L, // 3 //
				StrategicMappingCommons.CLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, ORCHARD_PK, //
				StrategicMappingCommons.CLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, "Glenmore @ Summit") //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_0, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("103041"), // Glenmore AT Summit (NB) <= CONTINUE
								Stops.getALL_STOPS().get("103168"), // ++ Summit at Dilworth (EB)
								Stops.getALL_STOPS().get("103079") // Orchard Park Exchange Bay G
						)) //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_1, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("103079"), // Orchard Park Exchange Bay G
								Stops.getALL_STOPS().get("103170"), // ++ Summit at Dilworth (WB)
								Stops.getALL_STOPS().get("103041") // Glenmore AT Summit (NB) => CONTINUE
						)) //
				.compileBothTripSort());
		map2.put(-1L, new RouteTripSpec(-1L, // 13 //
				StrategicMappingCommons.COUNTERCLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Country Club", //
				StrategicMappingCommons.COUNTERCLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, UBCO) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_0, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("140104"), // UBCO Exchange Bay E
								Stops.getALL_STOPS().get("104916"), // ++
								Stops.getALL_STOPS().get("103858") // Country Club 1740 block
						)) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_1, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("103858"), // Country Club 1740 block
								Stops.getALL_STOPS().get("103851"), // ++ Quail Ridge at Country Club
								Stops.getALL_STOPS().get("140104") // UBCO Exchange Bay D
						)) //
				.compileBothTripSort());
		map2.put(12L, new RouteTripSpec(12L, // 15 //
				StrategicMappingCommons.COUNTERCLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Crawford", //
				StrategicMappingCommons.COUNTERCLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, MISSION_REC_EXCH) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_0, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("103021"), // Mission Rec Exchange Bay A
								Stops.getALL_STOPS().get("103316"), // Dehart at Gordon (EB)
								Stops.getALL_STOPS().get("103401") // Westridge at Crawford (SB) => CONTINUE
						)) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_1, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("103401"), // Westridge at Crawford (SB) <= CONTINUE
								Stops.getALL_STOPS().get("103423"), // Westridge at Blueridge (SB)
								Stops.getALL_STOPS().get("103021") // Mission Rec Exchange Bay A
						)) //
				.compileBothTripSort());
		map2.put(13L, new RouteTripSpec(13L, // 16 //
				StrategicMappingCommons.COUNTERCLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Kettle Vly", //
				StrategicMappingCommons.COUNTERCLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, MISSION_REC_EXCH) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_0, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("103022"), // Mission Rec Exchange Bay B
								Stops.getALL_STOPS().get("103319"), // Lakeshore at Dehart (SB)
								Stops.getALL_STOPS().get("103808"), // Chute Lake at South Crest (SB)
								Stops.getALL_STOPS().get("103562") // Quilchena at Providence (SB) #KettleVly => CONTINUE
						)) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_1, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("103814"), // South Perimeter at Farron (EB) #KettleVly <= CONTINUE
								Stops.getALL_STOPS().get("103809"), // Chute Lake at South Crest (NB)
								Stops.getALL_STOPS().get("103317"), // Lakeshore at Dehart (NB)
								Stops.getALL_STOPS().get("103022") // Mission Rec Exchange Bay B
						)) //
				.compileBothTripSort());
		map2.put(14L, new RouteTripSpec(14L, // 17
				StrategicMappingCommons.CLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Southridge", //
				StrategicMappingCommons.CLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, MISSION_REC_EXCH) //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_0, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("103022"), // Mission Rec Exchange Bay B
								Stops.getALL_STOPS().get("103325"), // Gordon at Dehart (SB)
								Stops.getALL_STOPS().get("103191") // South Ridge at Frost (NB) => CONTINUE
						)) //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_1, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("103191"), // South Ridge at Frost (NB) <= CONTINUE
								Stops.getALL_STOPS().get("103820"), // Gordon at Raymer (NB)
								Stops.getALL_STOPS().get("103818"), // Gordon at Tozer (NB)
								Stops.getALL_STOPS().get("103022") // Mission Rec Exchange Bay B
						)) //
				.compileBothTripSort());
		map2.put(18L, new RouteTripSpec(18L, // 21
				StrategicMappingCommons.COUNTERCLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, GLENROSA, //
				StrategicMappingCommons.COUNTERCLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, WESTBANK) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_0, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("140006"), // Westbank Exchange Bay C
								Stops.getALL_STOPS().get("103624"), // McNair at Webber
								Stops.getALL_STOPS().get("103641") // Canary @ Blue Jay
						)) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_1, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("103641"), // Canary @ Blue Jay
								Stops.getALL_STOPS().get("103643"), // Glenrosa at Dunfield
								Stops.getALL_STOPS().get("140006") // Westbank Exchange Bay C
						)) //
				.compileBothTripSort());
		map2.put(25L, new RouteTripSpec(25L, // 29 //
				StrategicMappingCommons.COUNTERCLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Bear Crk", //
				StrategicMappingCommons.COUNTERCLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, "Boucherie Mtn") //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_0, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("140011"), // Boucherie Mountain Exchange Bay E
								Stops.getALL_STOPS().get("103115"), // Westlake at Horizon (EB)
								Stops.getALL_STOPS().get("103043") // Westside at Bear Creek (SB) => CONTINUE
						)) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_1, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("103043"), // Westside at Bear Creek (SB) <= CONTINUE
								Stops.getALL_STOPS().get("103078"), // Boucherie at Hayman (WB)
								Stops.getALL_STOPS().get("140011") // Boucherie Mountain Exchange Bay E
						)) //
				.compileBothTripSort());
		map2.put(26L, new RouteTripSpec(26L, // 32 //
				StrategicMappingCommons.CLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Main & Grant", //
				StrategicMappingCommons.CLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, "Shoreline & Stillwater") //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_0, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("140081"), // Shoreline at Stillwater (NB) <= CONTINUE ?
								Stops.getALL_STOPS().get("140088"), // Oceola at Pretty (SB) #LakewoodMall
								Stops.getALL_STOPS().get("103472") // Main at Grant Rd (NB)
						)) //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_1, //
						Arrays.asList(//
								Stops.getALL_STOPS().get("103472"), // Main at Grant Rd (NB)
								Stops.getALL_STOPS().get("103685"), // Oceola at Pretty (NB) #LakewoodMall
								Stops.getALL_STOPS().get("140081") // Shoreline at Stillwater (NB) => CONTINUE ?
						)) //
				.compileBothTripSort());
		ALL_ROUTE_TRIPS2 = map2;
	}

	@Override
	public int compareEarly(long routeId, List<MTripStop> list1, List<MTripStop> list2, MTripStop ts1, MTripStop ts2, GStop ts1GStop, GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop, this);
		}
		return super.compareEarly(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
	}

	@Override
	public ArrayList<MTrip> splitTrip(MRoute mRoute, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return ALL_ROUTE_TRIPS2.get(mRoute.getId()).getAllTrips();
		}
		return super.splitTrip(mRoute, gTrip, gtfs);
	}

	@Override
	public Pair<Long[], Integer[]> splitTripStop(MRoute mRoute, GTrip gTrip, GTripStop gTripStop, ArrayList<MTrip> splitTrips, GSpec routeGTFS) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()), this);
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	private final HashMap<Long, Long> routeIdToShortName = new HashMap<>();

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		final long rsn = Long.parseLong(mRoute.getShortName());
		this.routeIdToShortName.put(mRoute.getId(), rsn);
		if (rsn == 1L) {
			if (gTrip.getDirectionId() == 0) { // Downtown - NORTH
				if ("Downtown".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.NORTH);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Mission Rec Exch - SOUTH
				if (Arrays.asList( //
						"Lakeshore to OK College", //
						"Lakeshore - To OK College", //
						"Lakeshore - To South Pandosy", //
						"Lakeshore - Mission Rec Exch" //
				).contains(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.SOUTH);
					return;
				}
			}
		} else if (rsn == 4L) {
			if (gTrip.getDirectionId() == 0) { // UBCO - NORTH
				if ("UBCO Express".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.NORTH);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Pandosy - SOUTH
				if ("Pandosy Express".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.SOUTH);
					return;
				}
			}
		} else if (rsn == 5L) {
			if (gTrip.getDirectionId() == 0) { // Downtown - NORTH
				if ("Downtown".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.NORTH);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Mission Rec Exch - SOUTH
				if ("Gordon - Mission Rec Exch".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.SOUTH);
					return;
				}
			}
		} else if (rsn == 6L) {
			if (gTrip.getDirectionId() == 0) { // Downtown - INBOUND // Queensway Exch - INBOUND
				if ("Downtown".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.INBOUND);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // UBCO Exch - OUTBOUND
				if ("UBCO".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.OUTBOUND);
					return;
				}
			}
		} else if (rsn == 8L) {
			if (gTrip.getDirectionId() == 0) { // UBCO - NORTH
				if ("University - To UBCO".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "To Orchard Park".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.NORTH);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // OK College - SOUTH
				if ("OK College".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "University to OK College".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "To Orchard Park".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.SOUTH);
					return;
				}
			}
		} else if (rsn == 9L) {
			if (gTrip.getDirectionId() == 0) { // Downtown - WEST
				if ("Shopper Shuttle - To Downtown".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.WEST);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Orchard Park - EAST
				if ("Shopper Shuttle - To Orchard Park".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.EAST);
					return;
				}
			}
		} else if (rsn == 10L) {
			if (gTrip.getDirectionId() == 0) { // Queensway Exch - WEST
				if (Arrays.asList( //
						"North Rutland", // <>
						"Downtown", //
						"To Orchard Park" //
				).contains(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.WEST);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Rutland - EAST
				if ("North Rutland".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.EAST);
					return;
				}
			}
		} else if (rsn == 11L) {
			if (gTrip.getDirectionId() == 0) { // Downtown - WEST
				if (Arrays.asList( //
						"Downtown", //
						"to Orchard Park", //
						"To Orchard Park", //
						"Rutland to Orchard Park" //
				).contains(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.WEST);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Rutland - EAST
				if (Arrays.asList( //
						"Rutland To 14 Black Mtn", //
						"To Orchard Park", //
						"Rutland" //
				).contains(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.EAST);
					return;
				}
			}
		} else if (rsn == 12L) {
			if (gTrip.getDirectionId() == 0) { // S.Pandosy Exch - WEST
				if ("McCulloch - To Orchard Park".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "McCulloch - To South Pandosy".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "McCulloch to OK College".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.WEST);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // McCulloch - EAST
				if ("McCulloch".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.EAST);
					return;
				}
			}
		} else if (rsn == 14L) {
			if (gTrip.getDirectionId() == 0) { // Rutland Exch - NORTH
				if (Arrays.asList( //
						"Black Mountain", // <>
						"Black Mountain - To Rutland", //
						"Black Mountain - To Rutland Exch" //
				).contains(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.NORTH);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Black Mountain - SOUTH
				if ("Black Mountain".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.SOUTH);
					return;
				}
			}
		} else if (rsn == 18L) {
			if (gTrip.getDirectionId() == 0) { // Glenmore - NORTH
				if ("Glenmore".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.NORTH);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Downtown - SOUTH
				if ("Downtown".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.SOUTH);
					return;
				}
			}
		} else if (rsn == 19L) {
			if (gTrip.getDirectionId() == 0) { // Glenmore - NORTH
				if ("Glenmore".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.NORTH);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Orchard Pk - SOUTH
				if ("Orchard Park".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.SOUTH);
					return;
				}
			}
		} else if (rsn == 20L) {
			if (gTrip.getDirectionId() == 0) { // Westbank - WEST
				if ("Lakeview - To Westbank".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.WEST);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Boucherie Mtn - EAST
				if ("Lakeview - Boucherie Mtn".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.EAST);
					return;
				}
			}
		} else if (rsn == 22L) {
			if (gTrip.getDirectionId() == 0) { // Westbank - NORTH
				if ("Peachland - To Westbank".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "Peachland Exp - To Westbank".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.NORTH);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Peachland - SOUTH
				if ("Peachland".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.SOUTH);
					return;
				}
			}
		} else if (rsn == 23L) {
			if (gTrip.getDirectionId() == 0) { // Lake Country - NORTH
				if ("Lake Country".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "Lake Country Via Airport".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "Lake Country - Old Vernon Rd".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.NORTH);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // UBCO Exch - SOUTH
				if ("UBCO".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "UBCO Via Airport".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "UBCO - Old Vernon Rd".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.SOUTH);
					return;
				}
			}
		} else if (rsn == 24L) {
			if (gTrip.getDirectionId() == 0) { // Westbank - WEST
				if ("Shannon Lake - To Westbank".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "Shannon Ridge- To Westbank".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.WEST);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Boucherie Mtn - EAST
				if ("Shannon Lake - Boucherie Mtn".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "Shannon Ridge - Boucherie Mtn".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.EAST);
					return;
				}
			}
		} else if (rsn == 25L) {
			if (gTrip.getDirectionId() == 0) { // Westbank - WEST
				if ("East Boundary - To Westbank".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.WEST);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Boucherie Mtn - EAST
				if ("East Boundary - Boucherie Mtn".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.EAST);
					return;
				}
			}
		} else if (rsn == 26L) {
			if (gTrip.getDirectionId() == 0) { // Boucherie Mtn - NORTH
				if ("Old Okanagan - Boucherie Mtn".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.NORTH);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Westbank - SOUTH
				if ("East Boundary - To Westbank".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "Old Okanagan - Westbank Ex.".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.SOUTH);
					return;
				}
			}
		} else if (rsn == 28L) { //
			if (gTrip.getDirectionId() == 0) { // Boucherie Mtn - NORTH
				if ("Shannon Lake - Boucherie Mtn".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.NORTH);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Westbank - SOUTH
				if ("Shannon Lake - Westbank".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.SOUTH);
					return;
				}
			}
		} else if (rsn == 88L) { //
			if (gTrip.getDirectionId() == 1) { // Boucherie Mtn - OUTBOUND // Special - OUTBOUND
				if ("Special".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					if ("Special".equalsIgnoreCase(gTrip.getTripHeadsign())) {
						mTrip.setHeadsignString("Boucherie Mtn", StrategicMappingCommons.OUTBOUND);
						return;
					}
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.OUTBOUND);
					return;
				}
			}
		} else if (rsn == 97L) {
			if (gTrip.getDirectionId() == 0) { // Westbank - WEST
				if ("Westbank".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "To Cooper Stn".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "Downtown".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.WEST);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // UBCO - EAST
				if ("UBCO".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "Downtown".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.EAST);
					return;
				}
			}
		}
		MTLog.logFatal("%s: Unexpected trips head sign for %s!", mTrip.getRouteId(), gTrip);
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		if (MTrip.mergeEmpty(mTrip, mTripToMerge)) {
			return true;
		}
		List<String> headsignsValues = Arrays.asList(mTrip.getHeadsignValue(), mTripToMerge.getHeadsignValue());
		final long rsn = this.routeIdToShortName.get(mTrip.getRouteId());
		if (rsn == 1L) {
			if (Arrays.asList( //
					OK_COLLEGE, //
					"South Pandosy", //
					MISSION_REC_EXCH //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(MISSION_REC_EXCH, mTrip.getHeadsignId());
				return true;
			}
		} else if (rsn == 8L) {
			if (Arrays.asList( //
					ORCHARD_PK, // <>
					OK_COLLEGE //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(OK_COLLEGE, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					ORCHARD_PK, // <>
					UBCO //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(UBCO, mTrip.getHeadsignId());
				return true;
			}
		} else if (rsn == 10L) {
			if (Arrays.asList( //
					"North Rutland", // <>
					ORCHARD_PK, //
					DOWNTOWN //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (rsn == 11L) {
			if (Arrays.asList( //
					ORCHARD_PK, // <>
					BLACK_MOUNTAIN, //
					RUTLAND //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(RUTLAND, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					ORCHARD_PK, // <>
					DOWNTOWN //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(DOWNTOWN, mTrip.getHeadsignId());
				return true;
			}
		} else if (rsn == 12L) {
			if (Arrays.asList( //
					ORCHARD_PK, //
					OK_COLLEGE, //
					SOUTH_PANDOSY //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(SOUTH_PANDOSY, mTrip.getHeadsignId());
				return true;
			}
		} else if (rsn == 14L) {
			if (Arrays.asList( //
					"Black Mtn", // <>
					"Rutland Exch" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Rutland Exch", mTrip.getHeadsignId());
				return true;
			}
		} else if (rsn == 23L) {
			if (Arrays.asList( //
					"Old Vernon Rd", // <>
					LK_COUNTRY //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(LK_COUNTRY, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					"Old Vernon Rd", // <>
					UBCO //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(UBCO, mTrip.getHeadsignId());
				return true;
			}
		} else if (rsn == 97L) {
			if (Arrays.asList( //
					DOWNTOWN, // <>
					"Cooper Stn", //
					WESTBANK //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(WESTBANK, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					DOWNTOWN, // <>
					UBCO //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(UBCO, mTrip.getHeadsignId());
				return true;
			}
		}
		MTLog.logFatal("Unexpected trips to merge %s & %s!", mTrip, mTripToMerge);
		return false;
	}

	private static final Pattern EXCHANGE = Pattern.compile("((^|\\W)(exchange|ex)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String EXCHANGE_REPLACEMENT = "$2" + EXCH + "$4";

	private static final Pattern STARTS_WITH_NUMBER = Pattern.compile("(^[\\d]+[\\S]*)", Pattern.CASE_INSENSITIVE);

	private static final Pattern STARTS_WITH_DASH = Pattern.compile("(^.* - )", Pattern.CASE_INSENSITIVE);

	private static final Pattern ENDS_WITH_EXPRESS = Pattern.compile("((\\W)(express)($))", Pattern.CASE_INSENSITIVE);

	private static final Pattern SPECIAL = Pattern.compile("((^|\\W)(special)(\\W|$))", Pattern.CASE_INSENSITIVE);

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = EXCHANGE.matcher(tripHeadsign).replaceAll(EXCHANGE_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = STARTS_WITH_NUMBER.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = STARTS_WITH_DASH.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = ENDS_WITH_EXPRESS.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = SPECIAL.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = CleanUtils.removePoints(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private static final Pattern STARTS_WITH_IMPL = Pattern.compile("(^(\\(-IMPL-\\)))", Pattern.CASE_INSENSITIVE);

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = STARTS_WITH_IMPL.matcher(gStopName).replaceAll(StringUtils.EMPTY);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = EXCHANGE.matcher(gStopName).replaceAll(EXCHANGE_REPLACEMENT);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public int getStopId(GStop gStop) { // used by GTFS-RT
		return super.getStopId(gStop);
	}
}
