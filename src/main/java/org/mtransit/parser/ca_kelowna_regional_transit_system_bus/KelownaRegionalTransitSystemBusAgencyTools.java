package org.mtransit.parser.ca_kelowna_regional_transit_system_bus;

import static org.mtransit.commons.Constants.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;

import java.util.regex.Pattern;

// https://www.bctransit.com/open-data
public class KelownaRegionalTransitSystemBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new KelownaRegionalTransitSystemBusAgencyTools().start(args);
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "Kelowna Regional TS";
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return false;  // used by GTFS-RT
	}

	@Override
	public @Nullable String getRouteIdCleanupRegex() {
		return "\\-[A-Z]+$";
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.cleanSlashes(routeLongName);
		routeLongName = CleanUtils.cleanNumbers(routeLongName);
		routeLongName = CleanUtils.cleanStreetTypes(routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR_GREEN = "34B233";// GREEN (from PDF Corporate Graphic Standards)
	// private static final String AGENCY_COLOR_BLUE = "002C77"; // BLUE (from PDF Corporate Graphic Standards)

	private static final String AGENCY_COLOR = AGENCY_COLOR_GREEN;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Nullable
	@Override
	public String provideMissingRouteColor(@NotNull GRoute gRoute) {
		final int rsn = Integer.parseInt(gRoute.getRouteShortName());
		switch (rsn) {
		// @formatter:off
		case 1: return "3FA0D6";
		case 2: return "7B9597";
		case 3: return "7B9597";
		case 4: return "7B9597";
		case 5: return "3FA0D6";
		case 6: return "7B9597";
		case 7: return "3FA0D6";
		case 8: return "3FA0D6";
		case 9: return "7B9597";
		case 10: return "3FA0D6";
		case 11: return "3FA0D6";
		case 12: return "7B9597";
		case 13: return "7B9597";
		case 14: return "7B9597";
		case 15: return "7B9597";
		case 16: return "7B9597";
		case 17: return "7B9597";
		case 18: return "7B9597";
		case 19: return "7B9597";
		case 20: return "7B9597";
		case 21: return "7B9597";
		case 22: return "7B9597";
		case 23: return "3FA0D6";
		case 24: return "7B9597";
		case 25: return "7B9597";
		case 27: return "7B9597";
		case 28: return "7B9597";
		case 29: return "7B9597";
		case 32: return "7B9597";
		case 90: return "7B9597";
		case 97: return "F17C15";
		// @formatter:on
		default:
			throw new MTLog.Fatal("Unexpected route color %s!", gRoute);
		}
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	private static final Pattern FIX_EXCHANGE = CleanUtils.cleanWord("ex");
	private static final String FIX_EXCHANGE_REPLACEMENT = CleanUtils.cleanWordsReplacement("exchange");

	private static final Pattern STARTS_WITH_NUMBER = Pattern.compile("(^\\d+\\S*)", Pattern.CASE_INSENSITIVE);

	private static final Pattern STARTS_WITH_DASH = Pattern.compile("(^.* - )", Pattern.CASE_INSENSITIVE);

	private static final Pattern ENDS_WITH_EXPRESS = Pattern.compile("((\\W)(express)($))", Pattern.CASE_INSENSITIVE);

	private static final Pattern SPECIAL = Pattern.compile("((^|\\W)(special)(\\W|$))", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = FIX_EXCHANGE.matcher(tripHeadsign).replaceAll(FIX_EXCHANGE_REPLACEMENT);
		tripHeadsign = STARTS_WITH_NUMBER.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = STARTS_WITH_DASH.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = ENDS_WITH_EXPRESS.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = SPECIAL.matcher(tripHeadsign).replaceAll(EMPTY);
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
		gStopName = FIX_EXCHANGE.matcher(gStopName).replaceAll(FIX_EXCHANGE_REPLACEMENT);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) { // used by GTFS-RT
		return super.getStopId(gStop);
	}
}
