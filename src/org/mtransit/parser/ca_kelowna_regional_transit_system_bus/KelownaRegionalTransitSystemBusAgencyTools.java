package org.mtransit.parser.ca_kelowna_regional_transit_system_bus;

import java.util.HashSet;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MSpec;
import org.mtransit.parser.mt.data.MTrip;

// http://bctransit.com/*/footer/open-data
// http://bctransit.com/servlet/bctransit/data/GTFS.zip
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
		System.out.printf("Generating Kelowna Regional Transit System bus data...\n");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("Generating Kelowna Regional Transit System bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	private static final String INCLUDE_AGENCY_ID = "7"; // Kelowna Regional Transit System only

	@Override
	public boolean excludeRoute(GRoute gRoute) {
		if (!INCLUDE_AGENCY_ID.equals(gRoute.agency_id)) {
			return true;
		}
		return super.excludeRoute(gRoute);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
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
		return Long.parseLong(gRoute.route_short_name); // use route short name as route ID
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		String routeLongName = gRoute.route_long_name;
		routeLongName = MSpec.CLEAN_SLASHES.matcher(routeLongName).replaceAll(MSpec.CLEAN_SLASHES_REPLACEMENT);
		routeLongName = MSpec.cleanNumbers(routeLongName);
		routeLongName = MSpec.cleanStreetTypes(routeLongName);
		return MSpec.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR_GREEN = "34B233";// GREEN (from PDF Corporate Graphic Standards)

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
		if (StringUtils.isEmpty(gRoute.route_color)) {
			int rsn = Integer.parseInt(gRoute.route_short_name);
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

	private static final String EXCHANGE_SHORT = "Ex";
	private static final String MISSION_REC_EXCH = "Mission Rec " + EXCHANGE_SHORT;
	private static final String LOOP = "Loop";
	private static final String UBCO_EXCH = "UBCO " + EXCHANGE_SHORT;
	private static final String RUTLAND = "Rutland";
	private static final String QUEENSWAY_EXCH = "Queensway " + EXCHANGE_SHORT;
	private static final String OK_COLLEGE_EXCH = "OK College " + EXCHANGE_SHORT;
	private static final String ORCHARD_PK = "Orchard Pk";
	private static final String BLACK_MTN = "Black Mtn";
	private static final String GLENROSA = "Glenrosa";
	private static final String LK_COUNTRY = "Lk Country";
	private static final String WESTBANK_EXCH = "Westbank " + EXCHANGE_SHORT;

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (mRoute.id == 1l) {
			if (gTrip.direction_id == 1) {
				mTrip.setHeadsignString(MISSION_REC_EXCH, gTrip.direction_id);
				return;
			}
		} else if (mRoute.id == 3l) {
			if (gTrip.direction_id == 0) {
				mTrip.setHeadsignString(LOOP, gTrip.direction_id);
				return;
			}
		} else if (mRoute.id == 8l) {
			if (gTrip.direction_id == 0) {
				mTrip.setHeadsignString(UBCO_EXCH, gTrip.direction_id);
				return;
			} else if (gTrip.direction_id == 1) {
				mTrip.setHeadsignString(OK_COLLEGE_EXCH, gTrip.direction_id);
				return;
			}
		} else if (mRoute.id == 10l) {
			if (gTrip.direction_id == 0) {
				mTrip.setHeadsignString(RUTLAND, gTrip.direction_id);
				return;
			} else if (gTrip.direction_id == 1) {
				mTrip.setHeadsignString(QUEENSWAY_EXCH, gTrip.direction_id);
				return;
			}
		} else if (mRoute.id == 11l) {
			if (gTrip.direction_id == 0) {
				mTrip.setHeadsignString(RUTLAND, gTrip.direction_id);
				return;
			} else if (gTrip.direction_id == 1) {
				mTrip.setHeadsignString(QUEENSWAY_EXCH, gTrip.direction_id);
				return;
			}
		} else if (mRoute.id == 12l) {
			if (gTrip.direction_id == 1) {
				mTrip.setHeadsignString(OK_COLLEGE_EXCH, gTrip.direction_id);
				return;
			}
		} else if (mRoute.id == 14l) {
			if (gTrip.direction_id == 0) {
				mTrip.setHeadsignString(ORCHARD_PK, gTrip.direction_id);
				return;
			} else if (gTrip.direction_id == 1) {
				mTrip.setHeadsignString(BLACK_MTN, gTrip.direction_id);
				return;
			}
		} else if (mRoute.id == 21l) {
			if (gTrip.direction_id == 1) {
				mTrip.setHeadsignString(GLENROSA, gTrip.direction_id);
				return;
			}
		} else if (mRoute.id == 23l) {
			if (gTrip.direction_id == 1) {
				mTrip.setHeadsignString(LK_COUNTRY, gTrip.direction_id);
				return;
			}
		} else if (mRoute.id == 97l) {
			if (gTrip.direction_id == 1) {
				mTrip.setHeadsignString(WESTBANK_EXCH, gTrip.direction_id);
				return;
			}
		}
		mTrip.setHeadsignString(cleanTripHeadsign(gTrip.trip_headsign), gTrip.direction_id);
	}

	private static final Pattern EXCHANGE = Pattern.compile("(exchange)", Pattern.CASE_INSENSITIVE);
	private static final String EXCHANGE_REPLACEMENT = EXCHANGE_SHORT;

	private static final Pattern STARTS_WITH_NUMBER = Pattern.compile("(^[\\d]+[\\S]*)", Pattern.CASE_INSENSITIVE);

	private static final Pattern ENDS_WITH_VIA = Pattern.compile("( via .*$)", Pattern.CASE_INSENSITIVE);
	private static final Pattern STARTS_WITH_TO = Pattern.compile("(^.* to )", Pattern.CASE_INSENSITIVE);

	private static final Pattern AND = Pattern.compile("( and )", Pattern.CASE_INSENSITIVE);
	private static final String AND_REPLACEMENT = " & ";

	private static final Pattern CLEAN_P1 = Pattern.compile("[\\s]*\\([\\s]*");
	private static final String CLEAN_P1_REPLACEMENT = " (";
	private static final Pattern CLEAN_P2 = Pattern.compile("[\\s]*\\)[\\s]*");
	private static final String CLEAN_P2_REPLACEMENT = ") ";

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = EXCHANGE.matcher(tripHeadsign).replaceAll(EXCHANGE_REPLACEMENT);
		tripHeadsign = ENDS_WITH_VIA.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = STARTS_WITH_TO.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = AND.matcher(tripHeadsign).replaceAll(AND_REPLACEMENT);
		tripHeadsign = CLEAN_P1.matcher(tripHeadsign).replaceAll(CLEAN_P1_REPLACEMENT);
		tripHeadsign = CLEAN_P2.matcher(tripHeadsign).replaceAll(CLEAN_P2_REPLACEMENT);
		tripHeadsign = STARTS_WITH_NUMBER.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = MSpec.cleanStreetTypes(tripHeadsign);
		return MSpec.cleanLabel(tripHeadsign);
	}

	private static final Pattern STARTS_WITH_BOUND = Pattern.compile("(^(east|west|north|south)bound)", Pattern.CASE_INSENSITIVE);

	private static final Pattern AT = Pattern.compile("( at )", Pattern.CASE_INSENSITIVE);
	private static final String AT_REPLACEMENT = " / ";

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = STARTS_WITH_BOUND.matcher(gStopName).replaceAll(StringUtils.EMPTY);
		gStopName = AT.matcher(gStopName).replaceAll(AT_REPLACEMENT);
		gStopName = EXCHANGE.matcher(gStopName).replaceAll(EXCHANGE_REPLACEMENT);
		gStopName = MSpec.cleanStreetTypes(gStopName);
		return MSpec.cleanLabel(gStopName);
	}
}
