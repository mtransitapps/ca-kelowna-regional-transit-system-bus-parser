package org.mtransit.parser.ca_kelowna_regional_transit_system_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.StringUtils;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;

import java.util.HashSet;
import java.util.regex.Pattern;

import static org.mtransit.parser.Constants.EMPTY;

// https://www.bctransit.com/open-data
// https://kelowna.mapstrat.com/current/google_transit.zip
public class KelownaRegionalTransitSystemBusAgencyTools extends DefaultAgencyTools {

	public static void main(@Nullable String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-kelowna-regional-transit-system-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new KelownaRegionalTransitSystemBusAgencyTools().start(args);
	}

	@Nullable
	private HashSet<Integer> serviceIdInts;

	@Override
	public void start(@NotNull String[] args) {
		MTLog.log("Generating Kelowna Regional TS bus data...");
		long start = System.currentTimeMillis();
		this.serviceIdInts = extractUsefulServiceIdInts(args, this, true);
		super.start(args);
		MTLog.log("Generating Kelowna Regional TS bus data... DONE in %s.", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludingAll() {
		return this.serviceIdInts != null && this.serviceIdInts.isEmpty();
	}

	@Override
	public boolean excludeCalendar(@NotNull GCalendar gCalendar) {
		if (this.serviceIdInts != null) {
			return excludeUselessCalendarInt(gCalendar, this.serviceIdInts);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(@NotNull GCalendarDate gCalendarDates) {
		if (this.serviceIdInts != null) {
			return excludeUselessCalendarDateInt(gCalendarDates, this.serviceIdInts);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(@NotNull GTrip gTrip) {
		if (this.serviceIdInts != null) {
			return excludeUselessTripInt(gTrip, this.serviceIdInts);
		}
		return super.excludeTrip(gTrip);
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public long getRouteId(@NotNull GRoute gRoute) {  // used by GTFS-RT
		return super.getRouteId(gRoute); // used by GTFS-RT
	}

	@NotNull
	@Override
	public String getRouteLongName(@NotNull GRoute gRoute) {
		String routeLongName = gRoute.getRouteLongNameOrDefault();
		if (StringUtils.isEmpty(routeLongName)) {
			routeLongName = gRoute.getRouteDesc();
		}
		if (StringUtils.isEmpty(routeLongName)) {
			throw new MTLog.Fatal("Unexpected route long name for %s!", gRoute);
		}
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		routeLongName = CleanUtils.cleanNumbers(routeLongName);
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	private static final String AGENCY_COLOR_GREEN = "34B233";// GREEN (from PDF Corporate Graphic Standards)
	// private static final String AGENCY_COLOR_BLUE = "002C77"; // BLUE (from PDF Corporate Graphic Standards)

	private static final String AGENCY_COLOR = AGENCY_COLOR_GREEN;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String COLOR_3FA0D6 = "3FA0D6";
	private static final String COLOR_7B9597 = "7B9597";
	private static final String COLOR_F17C15 = "F17C15";

	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Nullable
	@Override
	public String getRouteColor(@NotNull GRoute gRoute) {
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
				throw new MTLog.Fatal("Unexpected route color %s!", gRoute);
			}
		}
		return super.getRouteColor(gRoute);
	}

	private static final String EXCH = "Exch";

	@Override
	public void setTripHeadsign(@NotNull MRoute mRoute, @NotNull MTrip mTrip, @NotNull GTrip gTrip, @NotNull GSpec gtfs) {
		mTrip.setHeadsignString(
				cleanTripHeadsign(gTrip.getTripHeadsignOrDefault()),
				gTrip.getDirectionIdOrDefault()
		);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	@Override
	public boolean mergeHeadsign(@NotNull MTrip mTrip, @NotNull MTrip mTripToMerge) {
		throw new MTLog.Fatal("Unexpected trips to merge %s & %s!", mTrip, mTripToMerge);
	}

	private static final Pattern EXCHANGE = Pattern.compile("((^|\\W)(exchange|ex)(\\W|$))", Pattern.CASE_INSENSITIVE);
	private static final String EXCHANGE_REPLACEMENT = "$2" + EXCH + "$4";

	private static final Pattern STARTS_WITH_NUMBER = Pattern.compile("(^[\\d]+[\\S]*)", Pattern.CASE_INSENSITIVE);

	private static final Pattern STARTS_WITH_DASH = Pattern.compile("(^.* - )", Pattern.CASE_INSENSITIVE);

	private static final Pattern ENDS_WITH_EXPRESS = Pattern.compile("((\\W)(express)($))", Pattern.CASE_INSENSITIVE);

	private static final Pattern SPECIAL = Pattern.compile("((^|\\W)(special)(\\W|$))", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = EXCHANGE.matcher(tripHeadsign).replaceAll(EXCHANGE_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = STARTS_WITH_NUMBER.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = STARTS_WITH_DASH.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = ENDS_WITH_EXPRESS.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = SPECIAL.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.removePoints(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private static final Pattern STARTS_WITH_DCOM = Pattern.compile("(^(\\(-DCOM-\\)))", Pattern.CASE_INSENSITIVE);
	private static final Pattern STARTS_WITH_IMPL = Pattern.compile("(^(\\(-IMPL-\\)))", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = STARTS_WITH_DCOM.matcher(gStopName).replaceAll(EMPTY);
		gStopName = STARTS_WITH_IMPL.matcher(gStopName).replaceAll(EMPTY);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = EXCHANGE.matcher(gStopName).replaceAll(EXCHANGE_REPLACEMENT);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) { // used by GTFS-RT
		return super.getStopId(gStop);
	}
}
