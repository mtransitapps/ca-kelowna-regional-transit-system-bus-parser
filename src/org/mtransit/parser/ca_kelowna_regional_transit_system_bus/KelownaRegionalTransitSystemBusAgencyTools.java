package org.mtransit.parser.ca_kelowna_regional_transit_system_bus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.commons.StrategicMappingCommons;
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

// https://www.bctransit.com/open-data
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

	private static final String INCLUDE_AGENCY_ID = "1"; // Kelowna Regional Transit System only

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
		if (StringUtils.isEmpty(routeLongName)) {
			routeLongName = gRoute.getRouteDesc();
		}
		if (StringUtils.isEmpty(routeLongName)) {
			System.out.printf("\nUnexptected route long name for %s!\n", gRoute);
			System.exit(-1);
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
	private static final String SOUTH_PANDOSY = "South Pandosy";
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
		map2.put(2L, new RouteTripSpec(2L, //
				StrategicMappingCommons.CLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, QUEENSWAY_EXCH, //
				StrategicMappingCommons.CLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, "Cambridge & Ellis") //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_0, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("102932"), // Cambridge at Ellis (EB) <= CONTINUE
								Stops.ALL_STOPS.get("102895"), // ++ Richter at Gaston (SB)
								Stops.ALL_STOPS.get("102859"), Stops2.ALL_STOPS2.get("102859"), // Queensway Exchange Bay E
						})) //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_1, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("102859"), Stops2.ALL_STOPS2.get("102859"), // Queensway Exchange Bay E
								Stops.ALL_STOPS.get("102888"), // ++ Ellis at Industrial (NB)
								Stops.ALL_STOPS.get("102932"), // Cambridge at Ellis (EB) => CONTINUE
						})) //
				.compileBothTripSort());
		map2.put(3L, new RouteTripSpec(3L, //
				StrategicMappingCommons.CLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, ORCHARD_PK, //
				StrategicMappingCommons.CLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, "Glenmore @ Summit") //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_0, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103041"), // Glenmore AT Summit (NB) <= CONTINUE
								Stops.ALL_STOPS.get("103168"), // ++ Summit at Dilworth (EB)
								Stops.ALL_STOPS.get("103079"), // Orchard Park Exchange Bay G
						})) //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_1, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103079"), // Orchard Park Exchange Bay G
								Stops.ALL_STOPS.get("103170"), // ++ Summit at Dilworth (WB)
								Stops.ALL_STOPS.get("103041"), // Glenmore AT Summit (NB) => CONTINUE
						})) //
				.compileBothTripSort());
		map2.put(6L, new RouteTripSpec(6L, //
				StrategicMappingCommons.INBOUND, MTrip.HEADSIGN_TYPE_STRING, QUEENSWAY_EXCH, //
				StrategicMappingCommons.OUTBOUND, MTrip.HEADSIGN_TYPE_STRING, UBCO_EXCH) //
				.addTripSort(StrategicMappingCommons.INBOUND, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("140099"), // UBCO Exchange Bay A
								Stops.ALL_STOPS.get("103005"), // ++
								Stops.ALL_STOPS.get("102856"), // Queensway Exchange Bay H
						})) //
				.addTripSort(StrategicMappingCommons.OUTBOUND, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("102856"), // Queensway Exchange Bay H
								Stops.ALL_STOPS.get("103010"), // ++
								Stops.ALL_STOPS.get("140100"), // Transit Way at Alumni Ave
						})) //
				.compileBothTripSort());
		map2.put(10L, new RouteTripSpec(10L, //
				StrategicMappingCommons.EAST, MTrip.HEADSIGN_TYPE_STRING, RUTLAND, //
				StrategicMappingCommons.WEST, MTrip.HEADSIGN_TYPE_STRING, QUEENSWAY_EXCH) //
				.addTripSort(StrategicMappingCommons.EAST, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("102854"), // Queensway Exchange Bay J
								Stops.ALL_STOPS.get("140151"), Stops2.ALL_STOPS2.get("140151"), // Rutland Exchange Bay E
								Stops.ALL_STOPS.get("103292"), // != Franklyn at McCurdy
								Stops.ALL_STOPS.get("103283"), // <> McCurdy at Mayfair
								Stops.ALL_STOPS.get("103327"), // <> Fitzpatrick at Findlay => CONTINUE
						})) //
				.addTripSort(StrategicMappingCommons.WEST, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103430"), // McCurdy Rd E at Craig <= CONTINUE
								Stops.ALL_STOPS.get("103396"), // != McCurdy Rd at Keyes
								Stops.ALL_STOPS.get("103283"), // <> McCurdy at Mayfair
								Stops.ALL_STOPS.get("103327"), // <> Fitzpatrick at Findlay
								Stops.ALL_STOPS.get("103343"), // != Fitzpatrick at Chichester
								Stops.ALL_STOPS.get("140149"), Stops2.ALL_STOPS2.get("140149"), // Rutland Exchange Bay C
								Stops.ALL_STOPS.get("103070"), // Orchard Park Exchange Bay B
								Stops.ALL_STOPS.get("102854"), // Queensway Exchange Bay J
						})) //
				.compileBothTripSort());
		map2.put(13L, new RouteTripSpec(13L, //
				StrategicMappingCommons.COUNTERCLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Country Club", //
				StrategicMappingCommons.COUNTERCLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, UBCO) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_0, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("140104"), // UBCO Exchange Bay E
								Stops.ALL_STOPS.get("104916"), // ++
								Stops.ALL_STOPS.get("103858"), // Country Club 1740 block
						})) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_1, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103858"), // Country Club 1740 block
								Stops.ALL_STOPS.get("103851"), // ++ Quail Ridge at Country Club
								Stops.ALL_STOPS.get("140104"), // UBCO Exchange Bay D
						})) //
				.compileBothTripSort());
		map2.put(15L, new RouteTripSpec(15L, //
				StrategicMappingCommons.COUNTERCLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Crawford", //
				StrategicMappingCommons.COUNTERCLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, MISSION_REC_EXCH) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_0, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103021"), // Mission Rec Exchange Bay A
								Stops.ALL_STOPS.get("103316"), // Dehart at Gordon (EB)
								Stops.ALL_STOPS.get("103401"), // Westridge at Crawford (SB) => CONTINUE
						})) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_1, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103401"), // Westridge at Crawford (SB) <= CONTINUE
								Stops.ALL_STOPS.get("103423"), // Westridge at Blueridge (SB)
								Stops.ALL_STOPS.get("103021"), // Mission Rec Exchange Bay A
						})) //
				.compileBothTripSort());
		map2.put(16L, new RouteTripSpec(16L, //
				StrategicMappingCommons.COUNTERCLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Kettle Vly", //
				StrategicMappingCommons.COUNTERCLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, MISSION_REC_EXCH) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_0, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103022"), // Mission Rec Exchange Bay B
								Stops.ALL_STOPS.get("103319"), // Lakeshore at Dehart (SB)
								Stops.ALL_STOPS.get("103808"), // Chute Lake at South Crest (SB)
								Stops.ALL_STOPS.get("103562"), // Quilchena at Providence (SB) #KettleVly => CONTINUE
						})) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_1, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103814"), // South Perimeter at Farron (EB) #KettleVly <= CONTINUE
								Stops.ALL_STOPS.get("103809"), // Chute Lake at South Crest (NB)
								Stops.ALL_STOPS.get("103317"), // Lakeshore at Dehart (NB)
								Stops.ALL_STOPS.get("103022"), // Mission Rec Exchange Bay B
						})) //
				.compileBothTripSort());
		map2.put(17L, new RouteTripSpec(17L, //
				StrategicMappingCommons.CLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Southridge", //
				StrategicMappingCommons.CLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, MISSION_REC_EXCH) //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_0, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103022"), // Mission Rec Exchange Bay B
								Stops.ALL_STOPS.get("103325"), // Gordon at Dehart (SB)
								Stops.ALL_STOPS.get("103191"), // South Ridge at Frost (NB) => CONTINUE
						})) //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_1, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103191"), // South Ridge at Frost (NB) <= CONTINUE
								Stops.ALL_STOPS.get("103820"), // Gordon at Raymer (NB)
								Stops.ALL_STOPS.get("103818"), // Gordon at Tozer (NB)
								Stops.ALL_STOPS.get("103022"), // Mission Rec Exchange Bay B
						})) //
				.compileBothTripSort());
		map2.put(19L, new RouteTripSpec(19L, //
				StrategicMappingCommons.NORTH, MTrip.HEADSIGN_TYPE_STRING, "Glenmore", //
				StrategicMappingCommons.SOUTH, MTrip.HEADSIGN_TYPE_STRING, ORCHARD_PK) //
				.addTripSort(StrategicMappingCommons.NORTH, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103079"), // Orchard Park Exchange Bay G
								Stops.ALL_STOPS.get("103041"), // ++
								Stops.ALL_STOPS.get("140133"), // Glenmore at Union
						})) //
				.addTripSort(StrategicMappingCommons.SOUTH, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("140133"), // Glenmore at Union
								Stops.ALL_STOPS.get("103013"), // ++
								Stops.ALL_STOPS.get("103079"), // Orchard Park Exchange Bay G
						})) //
				.compileBothTripSort());
		map2.put(21L, new RouteTripSpec(21L, //
				StrategicMappingCommons.COUNTERCLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, GLENROSA, //
				StrategicMappingCommons.COUNTERCLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, WESTBANK) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_0, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("140006"), // Westbank Exchange Bay C
								Stops.ALL_STOPS.get("103624"), // McNair at Webber
								Stops.ALL_STOPS.get("103641"), // Canary @ Blue Jay
						})) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_1, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103641"), // Canary @ Blue Jay
								Stops.ALL_STOPS.get("103643"), // Glenrosa at Dunfield
								Stops.ALL_STOPS.get("140006"), // Westbank Exchange Bay C
						})) //
				.compileBothTripSort());
		map2.put(22L, new RouteTripSpec(22L, //
				StrategicMappingCommons.SOUTH, MTrip.HEADSIGN_TYPE_STRING, PEACHLAND, //
				StrategicMappingCommons.NORTH, MTrip.HEADSIGN_TYPE_STRING, WESTBANK) //
				.addTripSort(StrategicMappingCommons.SOUTH, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("140006"), // != Westbank Exchange Bay C
								Stops.ALL_STOPS.get("103643"), // != Glenrosa at Dunfield
								Stops.ALL_STOPS.get("103682"), // !=
								Stops.ALL_STOPS.get("103686"), // <>
								Stops.ALL_STOPS.get("103689"), // !=
								Stops.ALL_STOPS.get("103701"), // !=
								Stops.ALL_STOPS.get("103702"), // ==
								Stops.ALL_STOPS.get("103844"), // Princeton at Pierce #Peachland
						})) //
				.addTripSort(StrategicMappingCommons.NORTH, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103844"), // Princeton at Pierce #Peachland
								Stops.ALL_STOPS.get("103690"), // == !=
								Stops.ALL_STOPS.get("103686"), // != <>
								Stops.ALL_STOPS.get("103675"), // == !=
								Stops.ALL_STOPS.get("140006"), // Westbank Exchange Bay C
						})) //
				.compileBothTripSort());
		map2.put(23L, new RouteTripSpec(23L, //
				StrategicMappingCommons.SOUTH, MTrip.HEADSIGN_TYPE_STRING, UBCO, //
				StrategicMappingCommons.NORTH, MTrip.HEADSIGN_TYPE_STRING, LK_COUNTRY) //
				.addTripSort(StrategicMappingCommons.SOUTH, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103660"), // Berry at Bottom Wood Lake
								Stops.ALL_STOPS.get("103652"), // ==
								Stops.ALL_STOPS.get("103609"), // !=
								Stops.ALL_STOPS.get("103656"), // !=
								Stops.ALL_STOPS.get("103619"), // !=
								Stops.ALL_STOPS.get("140106"), // !=
								Stops.ALL_STOPS.get("104915"), // ==
								Stops.ALL_STOPS.get("103847"), // ==
								Stops.ALL_STOPS.get("140098"), // != UBCO Exchange =>
								Stops.ALL_STOPS.get("140103"), // != UBCO Exchange Bay D
						})) //
				.addTripSort(StrategicMappingCommons.NORTH, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("140103"), // == UBCO Exchange Bay D
								Stops.ALL_STOPS.get("140159"), // !== Innovation at University
								Stops.ALL_STOPS.get("103846"), // != Innovation at Fleet
								Stops.ALL_STOPS.get("104916"), // !=
								Stops.ALL_STOPS.get("103680"), // !=
								Stops.ALL_STOPS.get("103610"), // !== 6240 Blk Airport Way
								Stops.ALL_STOPS.get("140105"), // !== Edwards Rd at Hwy 97
								Stops.ALL_STOPS.get("103480"), // !=
								Stops.ALL_STOPS.get("103620"), // !== Old Vernon at Spencer
								Stops.ALL_STOPS.get("103611"), // == Hwy 97 at Old Vernon
								Stops.ALL_STOPS.get("103650"), // == Hwy 97 at Parkinson
								Stops.ALL_STOPS.get("103472"), // Main at Grant Rd
								Stops.ALL_STOPS.get("103685"), // Oceola at Pretty #LakeCountry
								Stops.ALL_STOPS.get("103660"), // Berry at Bottom Wood Lake
						})) //
				.compileBothTripSort());
		map2.put(28L, new RouteTripSpec(28L, //
				StrategicMappingCommons.COUNTERCLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Smith Crk", //
				StrategicMappingCommons.COUNTERCLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, WESTBANK) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_0, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("140005"), // Westbank Exchange Bay B
								Stops.ALL_STOPS.get("103541"), // Old Okanagan 3330 block (NB)
								Stops.ALL_STOPS.get("140063"), // Copper Ridge at Smith (WB) => CONTINUE
						})) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_1, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("140063"), // Copper Ridge at Smith (WB) <= CONTINUE
								Stops.ALL_STOPS.get("103573"), // Elliott at Reece (SB)
								Stops.ALL_STOPS.get("140005"), // Westbank Exchange Bay B
						})) //
				.compileBothTripSort());
		map2.put(29L, new RouteTripSpec(29L, //
				StrategicMappingCommons.COUNTERCLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Bear Crk", //
				StrategicMappingCommons.COUNTERCLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, "Boucherie Mtn") //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_0, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("140011"), // Boucherie Mountain Exchange Bay E
								Stops.ALL_STOPS.get("103115"), // Westlake at Horizon (EB)
								Stops.ALL_STOPS.get("103043"), // Westside at Bear Creek (SB) => CONTINUE
						})) //
				.addTripSort(StrategicMappingCommons.COUNTERCLOCKWISE_1, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103043"), // Westside at Bear Creek (SB) <= CONTINUE
								Stops.ALL_STOPS.get("103078"), // Boucherie at Hayman (WB)
								Stops.ALL_STOPS.get("140011"), // Boucherie Mountain Exchange Bay E
						})) //
				.compileBothTripSort());
		map2.put(32L, new RouteTripSpec(32L, //
				StrategicMappingCommons.CLOCKWISE_0, MTrip.HEADSIGN_TYPE_STRING, "Main & Grant", //
				StrategicMappingCommons.CLOCKWISE_1, MTrip.HEADSIGN_TYPE_STRING, "Shoreline & Stillwater") //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_0, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("140081"), // Shoreline at Stillwater (NB) <= CONTINUE ?
								Stops.ALL_STOPS.get("140088"), // Oceola at Pretty (SB) #LakewoodMall
								Stops.ALL_STOPS.get("103472"), // Main at Grant Rd (NB)
						})) //
				.addTripSort(StrategicMappingCommons.CLOCKWISE_1, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103472"), // Main at Grant Rd (NB)
								Stops.ALL_STOPS.get("103685"), // Oceola at Pretty (NB) #LakewoodMall
								Stops.ALL_STOPS.get("140081"), // Shoreline at Stillwater (NB) => CONTINUE ?
						})) //
				.compileBothTripSort());
		map2.put(88L, new RouteTripSpec(88L, //
				StrategicMappingCommons.INBOUND, MTrip.HEADSIGN_TYPE_STRING, StringUtils.EMPTY, //
				StrategicMappingCommons.OUTBOUND, MTrip.HEADSIGN_TYPE_STRING, "Boucherie Mtn") //
				.addTripSort(StrategicMappingCommons.INBOUND, //
						Arrays.asList(new String[] { //
						/* no stops */
						})) //
				.addTripSort(StrategicMappingCommons.OUTBOUND, //
						Arrays.asList(new String[] { //
						Stops.ALL_STOPS.get("103054"), // KLO 1470 block (WB)
								Stops.ALL_STOPS.get("102860"), // Queensway Exchange Bay D
								Stops.ALL_STOPS.get("140009") // Boucherie Mountain Exchange Bay C
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
		if (mRoute.getId() == 1L) {
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
		} else if (mRoute.getId() == 4L) {
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
		} else if (mRoute.getId() == 5L) {
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
		} else if (mRoute.getId() == 8L) {
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
		} else if (mRoute.getId() == 9L) {
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
		} else if (mRoute.getId() == 11L) {
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
		} else if (mRoute.getId() == 12L) {
			if (gTrip.getDirectionId() == 0) { // S.Pandosy Exch - WEST
				if ("McCulloch - To Orchard Park".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "McCulloch - To South Pandosy".equalsIgnoreCase(gTrip.getTripHeadsign()) //
						|| "McCulloch to S.Pandosy Exch".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.WEST);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // McCulloch - EAST
				if ("McCulloch".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.EAST);
					return;
				}
			}
		} else if (mRoute.getId() == 14L) {
			if (gTrip.getDirectionId() == 0) { // Rutland Exch - NORTH
				if (Arrays.asList( //
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
		} else if (mRoute.getId() == 18L) {
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
		} else if (mRoute.getId() == 20L) {
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
		} else if (mRoute.getId() == 24L) {
			if (gTrip.getDirectionId() == 0) { // Westbank - WEST
				if ("Shannon Lake - To Westbank".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.WEST);
					return;
				}
			} else if (gTrip.getDirectionId() == 1) { // Boucherie Mtn - EAST
				if ("Shannon Lake - Boucherie Mtn".equalsIgnoreCase(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), StrategicMappingCommons.EAST);
					return;
				}
			}
		} else if (mRoute.getId() == 25L) {
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
		} else if (mRoute.getId() == 97L) {
			if (gTrip.getDirectionId() == 0) { // Westbank - WEST
				if ("Westbank".equalsIgnoreCase(gTrip.getTripHeadsign()) //
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
		System.out.printf("\n%s: Unexpected trips head sign for %s!\n", mTrip.getRouteId(), gTrip);
		System.exit(-1);
		return;
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		if (MTrip.mergeEmpty(mTrip, mTripToMerge)) {
			return true;
		}
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
			if (Arrays.asList( //
					ORCHARD_PK, //
					SOUTH_PANDOSY //
					).containsAll(headsignsValues)) {
				mTrip.setHeadsignString(SOUTH_PANDOSY, mTrip.getHeadsignId());
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

	private static final Pattern SPECIAL = Pattern.compile("((^|\\W){1}(special)(\\W|$){1})", Pattern.CASE_INSENSITIVE);

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
		tripHeadsign = SPECIAL.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = CleanUtils.removePoints(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private static final Pattern STARTS_WITH_IMPL = Pattern.compile("(^(\\(\\-IMPL\\-\\)))", Pattern.CASE_INSENSITIVE);
	private static final Pattern STARTS_WITH_BOUND = Pattern.compile("(^(east|west|north|south)bound)", Pattern.CASE_INSENSITIVE);

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = STARTS_WITH_IMPL.matcher(gStopName).replaceAll(StringUtils.EMPTY);
		gStopName = STARTS_WITH_BOUND.matcher(gStopName).replaceAll(StringUtils.EMPTY);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = EXCHANGE.matcher(gStopName).replaceAll(EXCHANGE_REPLACEMENT);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public int getStopId(GStop gStop) {
		return Integer.parseInt(gStop.getStopCode()); // use stop code as stop ID
	}
}
