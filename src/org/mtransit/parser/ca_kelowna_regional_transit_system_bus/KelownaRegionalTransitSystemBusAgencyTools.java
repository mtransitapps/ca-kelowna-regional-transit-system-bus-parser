package org.mtransit.parser.ca_kelowna_regional_transit_system_bus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Pair;
import org.mtransit.parser.SplitUtils;
import org.mtransit.parser.Utils;
import org.mtransit.parser.SplitUtils.RouteTripSpec;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.gtfs.data.GTripStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTripStop;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.mt.data.MTrip;

// https://bctransit.com/*/footer/open-data
// https://bctransit.com/servlet/bctransit/data/GTFS - Kelowna
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

	@Override
	public void start(String[] args) {
		System.out.printf("\nGenerating Kelowna Regional TS bus data...");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this, true);
		super.start(args);
		System.out.printf("\nGenerating Kelowna Regional TS bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludingAll() {
		return this.serviceIds != null && this.serviceIds.isEmpty();
	}

	private static final String INCLUDE_ONLY_SERVICE_ID_CONTAINS = null;

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (INCLUDE_ONLY_SERVICE_ID_CONTAINS != null && !gCalendar.getServiceId().contains(INCLUDE_ONLY_SERVICE_ID_CONTAINS)) {
			return true;
		}
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (INCLUDE_ONLY_SERVICE_ID_CONTAINS != null && !gCalendarDates.getServiceId().contains(INCLUDE_ONLY_SERVICE_ID_CONTAINS)) {
			return true;
		}
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	private static final String INCLUDE_AGENCY_ID = "7"; // Kelowna Regional Transit System only

	@Override
	public boolean excludeRoute(GRoute gRoute) {
		if (!INCLUDE_AGENCY_ID.equals(gRoute.getAgencyId())) {
			return true;
		}
		return super.excludeRoute(gRoute);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (INCLUDE_ONLY_SERVICE_ID_CONTAINS != null && !gTrip.getServiceId().contains(INCLUDE_ONLY_SERVICE_ID_CONTAINS)) {
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
	public long getRouteId(GRoute gRoute) {
		return Long.parseLong(gRoute.getRouteShortName()); // use route short name as route ID
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.getRouteLongName();
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		routeLongName = CleanUtils.cleanNumbers(routeLongName);
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR_GREEN = "34B233";// GREEN (from PDF Corporate Graphic Standards)
	private static final String AGENCY_COLOR_BLUE = "002C77"; // BLUE (from PDF Corporate Graphic Standards)

	private static final String AGENCY_COLOR = AGENCY_COLOR_GREEN;

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String COLOR_3FA0D6 = "3FA0D6";
	private static final String COLOR_7B9597 = "7B9597";
	private static final String COLOR_F17C15 = "F17C15";

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
				System.out.println("Unexpected route color " + gRoute);
				System.exit(-1);
				return null;
			}
		}
		return super.getRouteColor(gRoute);
	}

	private static final String EXCH = "Exch";

	private static final String BLACK_MOUNTAIN = "Black Mtn";
	private static final String S_PANDOSY = "S.Pandosy";
	private static final String S_PANDOSY_EXCH = S_PANDOSY + " " + EXCH;
	private static final String MISSION_REC_EXCH = "Mission Rec " + EXCH;
	private static final String UBCO = "UBCO";
	private static final String UBCO_EXCH = UBCO + " " + EXCH;
	private static final String RUTLAND = "Rutland";
	private static final String QUEENSWAY_EXCH = "Queensway " + EXCH;
	private static final String OK_COLLEGE = "OK College";
	private static final String ORCHARD_PK = "Orchard Pk";
	private static final String GLENROSA = "Glenrosa";
	private static final String LK_COUNTRY = "Lk Country";
	private static final String WESTBANK = "Westbank";
	private static final String PEACHLAND = "Peachland";

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;
	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<Long, RouteTripSpec>();
		map2.put(6L, new RouteTripSpec(6L, //
				0, MTrip.HEADSIGN_TYPE_STRING, QUEENSWAY_EXCH, //
				1, MTrip.HEADSIGN_TYPE_STRING, UBCO_EXCH) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"140099", // UBCO Exchange Bay A
								"103005", // ++
								"102856", // Queensway Exchange Bay H
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"102856", // Queensway Exchange Bay H
								"103010", // ++
								"140100", // Transit Way at Alumni Ave
						})) //
				.compileBothTripSort());
		map2.put(10L, new RouteTripSpec(10L, //
				0, MTrip.HEADSIGN_TYPE_STRING, RUTLAND, //
				1, MTrip.HEADSIGN_TYPE_STRING, QUEENSWAY_EXCH) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"102854", // Queensway Exchange Bay J
								"140151", // (-IMPL-)Rutland Exchange Bay E
								"103327", // Fitzpatrick at Findlay
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"103430", // McCurdy Rd E at Craig
								"103327", // Fitzpatrick at Findlay
								"140149", // (-IMPL-)Rutland Exchange Bay C
								"103070", // Orchard Park Exchange Bay B
								"102854", // Queensway Exchange Bay J
						})) //
				.compileBothTripSort());
		map2.put(13L, new RouteTripSpec(13L, //
				0, MTrip.HEADSIGN_TYPE_STRING, "Country Club", //
				1, MTrip.HEADSIGN_TYPE_STRING, UBCO) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"140104", // UBCO Exchange Bay E
								"104916", // ++
								"103858", // Country Club 1740 block
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"103858", // Country Club 1740 block
								"103851", // ++ Quail Ridge at Country Club
								"140103", // UBCO Exchange Bay D
						})) //
				.compileBothTripSort());
		map2.put(19L, new RouteTripSpec(19L, //
				0, MTrip.HEADSIGN_TYPE_STRING, "Glenmore", //
				1, MTrip.HEADSIGN_TYPE_STRING, ORCHARD_PK) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"103079", // Orchard Park Exchange Bay G
								"103041", // ++
								"140133", // (-IMPL-)Glenmore at Union
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"140133", // (-IMPL-)Glenmore at Union
								"103013", // ++
								"103079", // Orchard Park Exchange Bay G
						})) //
				.compileBothTripSort());
		map2.put(21L, new RouteTripSpec(21L, //
				0, MTrip.HEADSIGN_TYPE_STRING, "Blue Jay & Canary", //
				1, MTrip.HEADSIGN_TYPE_STRING, GLENROSA) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"140006", // Westbank Exchange Bay C
								"103624", // McNair at Webber
								"103641", // Canary @ Blue Jay
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"103641", // Canary @ Blue Jay
								"103643", // Glenrosa at Dunfield
								"140006", // Westbank Exchange Bay C
						})) //
				.compileBothTripSort());
		map2.put(22L, new RouteTripSpec(22L, //
				0, MTrip.HEADSIGN_TYPE_STRING, PEACHLAND, //
				1, MTrip.HEADSIGN_TYPE_STRING, WESTBANK) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"140006", // != Westbank Exchange Bay C
								"103643", // != Glenrosa at Dunfield
								"103682", // !=
								"103686", // <>
								"103689", // !=
								"103701", // !=
								"103702", // ==
								"103844", // Princeton at Pierce #Peachland
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"103844", // Princeton at Pierce #Peachland
								"103690", // == !=
								"103686", // != <>
								"103675", // == !=
								"140006", // Westbank Exchange Bay C
						})) //
				.compileBothTripSort());
		map2.put(23L, new RouteTripSpec(23L, //
				0, MTrip.HEADSIGN_TYPE_STRING, UBCO, //
				1, MTrip.HEADSIGN_TYPE_STRING, LK_COUNTRY) //
				.addTripSort(0, //
						Arrays.asList(new String[] { //
						"103685", // Oceola at Pretty #LakeCountry
								"103652", // ==
								"103609", // !=
								"103656", // !=
								"103619", // !=
								"140106", // !=
								"104915", // ==
								"103847", // ==
								"140098", // != Alumni Ave at Transit Way =>
								"140103", // != UBCO Exchange Bay D
						})) //
				.addTripSort(1, //
						Arrays.asList(new String[] { //
						"140103", // UBCO Exchange Bay D
								"103846", // !==
								"104916", // !=
								"103680", // !=
								"103610", // !==
								"103480", // !==
								"103620", // !==
								"103650", // ==
								"103472", // Main at Grant Rd
								"103685", // Oceola at Pretty #LakeCountry
						})) //
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

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), gTrip.getDirectionId());
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		List<String> headsignsValues = Arrays.asList(mTrip.getHeadsignValue(), mTripToMerge.getHeadsignValue());
		if (mTrip.getRouteId() == 1L) {
			if (Arrays.asList( //
					OK_COLLEGE, //
					"South Pandosy", //
					MISSION_REC_EXCH //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(MISSION_REC_EXCH, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 8L) {
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
		} else if (mTrip.getRouteId() == 11L) {
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
					"Downtown" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Downtown", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 12L) {
			if (Arrays.asList( //
					ORCHARD_PK, //
					S_PANDOSY_EXCH //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(S_PANDOSY_EXCH, mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 97L) {
			if (Arrays.asList( //
					"Downtown", // <>
					WESTBANK //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(WESTBANK, mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					"Downtown", // <>
					UBCO //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(UBCO, mTrip.getHeadsignId());
				return true;
			}
		}
		System.out.printf("\nUnexpected trips to merge %s & %s!\n", mTrip, mTripToMerge);
		System.exit(-1);
		return false;
	}

	private static final Pattern EXCHANGE = Pattern.compile("((^|\\W){1}(exchange|ex)(\\W|$){1})", Pattern.CASE_INSENSITIVE);
	private static final String EXCHANGE_REPLACEMENT = "$2" + EXCH + "$4";

	private static final Pattern STARTS_WITH_NUMBER = Pattern.compile("(^[\\d]+[\\S]*)", Pattern.CASE_INSENSITIVE);

	private static final Pattern STARTS_WITH_DASH = Pattern.compile("(^.* \\- )", Pattern.CASE_INSENSITIVE);

	private static final Pattern TO = Pattern.compile("((^|\\W){1}(to)(\\W|$){1})", Pattern.CASE_INSENSITIVE);
	private static final Pattern VIA = Pattern.compile("((^|\\W){1}(via)(\\W|$){1})", Pattern.CASE_INSENSITIVE);

	private static final Pattern AND = Pattern.compile("( and )", Pattern.CASE_INSENSITIVE);
	private static final String AND_REPLACEMENT = " & ";

	private static final Pattern CLEAN_P1 = Pattern.compile("[\\s]*\\([\\s]*");
	private static final String CLEAN_P1_REPLACEMENT = " (";
	private static final Pattern CLEAN_P2 = Pattern.compile("[\\s]*\\)[\\s]*");
	private static final String CLEAN_P2_REPLACEMENT = ") ";

	private static final Pattern ENDS_WITH_EXPRESS = Pattern.compile("((\\W){1}(express)($){1})", Pattern.CASE_INSENSITIVE);

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		Matcher matcherTO = TO.matcher(tripHeadsign);
		if (matcherTO.find()) {
			String gTripHeadsignAfterTO = tripHeadsign.substring(matcherTO.end());
			tripHeadsign = gTripHeadsignAfterTO;
		}
		Matcher matcherVIA = VIA.matcher(tripHeadsign);
		if (matcherVIA.find()) {
			String gTripHeadsignBeforeVIA = tripHeadsign.substring(0, matcherVIA.start());
			tripHeadsign = gTripHeadsignBeforeVIA;
		}
		tripHeadsign = EXCHANGE.matcher(tripHeadsign).replaceAll(EXCHANGE_REPLACEMENT);
		tripHeadsign = AND.matcher(tripHeadsign).replaceAll(AND_REPLACEMENT);
		tripHeadsign = CLEAN_P1.matcher(tripHeadsign).replaceAll(CLEAN_P1_REPLACEMENT);
		tripHeadsign = CLEAN_P2.matcher(tripHeadsign).replaceAll(CLEAN_P2_REPLACEMENT);
		tripHeadsign = STARTS_WITH_NUMBER.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = STARTS_WITH_DASH.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = ENDS_WITH_EXPRESS.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = CleanUtils.removePoints(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private static final Pattern STARTS_WITH_BOUND = Pattern.compile("(^(east|west|north|south)bound)", Pattern.CASE_INSENSITIVE);

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = STARTS_WITH_BOUND.matcher(gStopName).replaceAll(StringUtils.EMPTY);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = EXCHANGE.matcher(gStopName).replaceAll(EXCHANGE_REPLACEMENT);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}
}
