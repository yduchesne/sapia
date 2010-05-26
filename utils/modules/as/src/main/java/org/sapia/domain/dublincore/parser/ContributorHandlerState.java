package org.sapia.domain.dublincore.parser;


// Import of Sapia's domain classes
// --------------------------------
import org.sapia.domain.dublincore.Contributor;

// Import of Sapia's utility classes
// ---------------------------------
import org.sapia.util.xml.parser.HandlerContextIF;
import org.sapia.util.xml.parser.ParserUtil;

// Imports of David Meggison's SAX classes
// ---------------------------------------
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */
public class ContributorHandlerState extends AbstractDublinCoreHandlerState {
  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  CLASS ATTRIBUTES  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /** Indicates if this handler state is currently parsing. */
  private boolean _isParsing;

  /** Buffer that contains the characters of the element beign parsed. */
  private StringBuffer _theElementContent;

  /** The result object of this handler state. */
  private Contributor _theContributor;

  /////////////////////////////////////////////////////////////////////////////////////////
  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Creates a new ContributorHandlerState instance.
   */
  public ContributorHandlerState() {
    _isParsing           = false;
    _theElementContent   = new StringBuffer();
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Returns the result Contributor object of this handler state.
   *
   * @return The result Contributor object of this handler state.
   * @exception IllegalStateException If this handler state is currently parsing.
   */
  public Contributor getResult() {
    if (_isParsing == true) {
      throw new IllegalStateException("This handler state is currently parsing");
    }

    return _theContributor;
  }

  /////////////////////////////////////////////////////////////////////////////////////////
  ///////////////////////////////  INTERACE IMPLEMENTATION  ///////////////////////////////
  /////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Receives the notification of the the start of an element.
   *
   * @param aContext The handler context.
   * @param anUri The namespace URI associated with the element
   * @param aLocalName The element type local name.
   * @param aQualifiedName The element type qualified name.
   * @param someAttributes The specified or defaulted attributes.
   * @exception SAXException If an exception occurs.
   */
  public void startElement(HandlerContextIF aContext, String anUri,
    String aLocalName, String aQualifiedName, Attributes someAttributes)
    throws SAXException {
    // Validating the namespace URI of the call
    validateNamespace(anUri, aLocalName, aQualifiedName);

    if (ELEMENT_CONTRIBUTOR.equals(aLocalName)) {
      _isParsing = true;

      String anXmlLanguageCode = ParserUtil.extractXmlLanguageCode(someAttributes);
      _theContributor = new Contributor("", anXmlLanguageCode);
    }
  }

  /**
   * Receives the notification of the the end of an element.
   *
   * @param aContext The handler context.
   * @param anUri The namespace URI associated with the element
   * @param aLocalName The element type local name.
   * @param aQualifiedName The element type qualified name.
   * @exception SAXException If an exception occurs.
   */
  public void endElement(HandlerContextIF aContext, String anUri,
    String aLocalName, String aQualifiedName) throws SAXException {
    // Validating the namespace URI of the call
    validateNamespace(anUri, aLocalName, aQualifiedName);

    if (ELEMENT_CONTRIBUTOR.equals(aLocalName)) {
      _theContributor.setValue(_theElementContent.toString());
      _isParsing = false;
    }
  }

  /**
   * Receives the notification of character data inside an element.
   *
   * @param aContext The handler context.
   * @param someChars The characters.
   * @param anOffset The start position in the character array.
   * @param aLength The number of characters to use from the character array.
   * @exception SAXException If an exception occurs.
   */
  public void characters(HandlerContextIF aContext, char[] someChars,
    int anOffset, int length) throws SAXException {
    _theElementContent.append(someChars, anOffset, length);
  }

  /**
   * Receives the notification of ignorable whitespace in element content.
   *
   * @param aContext The handler context.
   * @param someChars The whitespace characters.
   * @param anOffset The start position in the character array.
   * @param aLength The number of characters to use from the character array.
   * @exception SAXException If an exception occurs.
   */
  public void ignorableWhitespace(HandlerContextIF aContext, char[] someChars,
    int anOffset, int aLength) throws SAXException {
    // IGNORING WHITESPACES...
  }
}


//public abstract class AbstractHotelMLHandlerState implements HotelMLDictionnary, HandlerStateIF {
//
//  /////////////////////////////////////////////////////////////////////////////////////////
//  //////////////////////////////////  CLASS ATTRIBUTES  ///////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////
//
//  /** Defines a class object for this class. */
//  private static final Class _thisClass = AbstractHotelMLHandlerState.class;
//
//  /** Defines the logger object for this class. */
//  private static final Logger _theLogger = Logger.getInstance().forClass(_thisClass);
//
//  /** Defines the pattern of a valid HotelML amount. */
//  private static final String HOTELML_AMOUNT_PATTERN = "0.00";
//
//  /////////////////////////////////////////////////////////////////////////////////////////
//  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////
//
//  /** The amount formatter of this handler. */
//  private NumberFormat _theAmountFormatter;
//
//  /** The target namespace of this handler. */
//  private String _theTargetNamespace;
//
//  /////////////////////////////////////////////////////////////////////////////////////////
//  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////
//
//  /**
//   * Creates a new AbstractHotelMLHandlerState instance with the arguments passed in.
//   */
//  protected AbstractHotelMLHandlerState(Locale aLocale, String aTargetNamespace) {
//    _theTargetNamespace = aTargetNamespace;
//    _theAmountFormatter = NumberFormat.getNumberInstance(aLocale);
//    ((DecimalFormat) _theAmountFormatter).applyLocalizedPattern(HOTELML_AMOUNT_PATTERN);
//  }
//
//  /////////////////////////////////////////////////////////////////////////////////////////
//  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////
//
//  /**
//   * Returns the targeted namespace of this handler state.
//   *
//   * @return The targeted namespace of this handler state.
//   */
//  public String getTargetNamespace() {
//    return _theTargetNamespace;
//  }
//
//  /////////////////////////////////////////////////////////////////////////////////////////
//  ///////////////////////////////////  HELPER METHODS  ////////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////
//
//  /**
//   * Helper methods that creates an ExtraBed object from the attributes
//   * passed in.
//   *
//   * @param someAttributes The attributes from the <ExtraBed> element.
//   * @return An extra bed object.
//   */
//  protected ExtraBed createExtraBed(Attributes someAttributes) {
//    // Extract the attribute's values
//    String aType = someAttributes.getValue(ATTRIBUTE_EXTRA_BED_TYPE);
//    String aCharge = someAttributes.getValue(ATTRIBUTE_EXTRA_BED_CHARGE);
//    String aCurrencyCode = someAttributes.getValue(ATTRIBUTE_EXTRA_BED_CURRENCY);
//    String aNumber = someAttributes.getValue(ATTRIBUTE_EXTRA_BED_NUMBER);
//
//    // Creating an extra bed object
//    ExtraBed anExtraBed = new ExtraBed(aType);
//
//    // Setting the extra charge
//    if (aCharge != null && aCharge.length() > 0) {
//      anExtraBed.setExtraCharge(parseAmount(aCharge));
//    }
//
//    // Setting the currency code
//    if (aCurrencyCode != null && aCurrencyCode.length() > 0) {
//      anExtraBed.setCurrencyCode(aCurrencyCode);
//    }
//
//    // Setting the number of beds
//    if (aNumber != null && aNumber.length() > 0) {
//      anExtraBed.setNumberOfBeds(parseInt(aNumber));
//    }
//
//    return anExtraBed;
//  }
//
//  /**
//   * Helper methods that creates an ExtraPerson object from the attributes
//   * passed in.
//   *
//   * @param someAttributes The attributes from the <ExtraPerson> element.
//   * @return An extra person object.
//   */
//  protected ExtraPerson createExtraPerson(Attributes someAttributes) {
//    // Extract the attribute's values
//    String aType = someAttributes.getValue(ATTRIBUTE_EXTRA_PERSON_TYPE);
//    String aCharge = someAttributes.getValue(ATTRIBUTE_EXTRA_PERSON_CHARGE);
//    String aCurrencyCode = someAttributes.getValue(ATTRIBUTE_EXTRA_PERSON_CURRENCY);
//    String aNumber = someAttributes.getValue(ATTRIBUTE_EXTRA_PERSON_NUMBER);
//
//    // Creating an extra person object
//    ExtraPerson anExtraPerson = new ExtraPerson(aType);
//
//    // Setting the extra charge
//    if (aCharge != null && aCharge.length() > 0) {
//      anExtraPerson.setCharge(parseAmount(aCharge));
//    }
//
//    // Setting the currency code
//    if (aCurrencyCode != null && aCurrencyCode.length() > 0) {
//      anExtraPerson.setCurrencyCode(aCurrencyCode);
//    }
//
//    // Setting the number
//    if (aNumber != null && aNumber.length() > 0) {
//      anExtraPerson.setNumber(parseInt(aNumber));
//    }
//
//    return anExtraPerson;
//  }
//
//  /**
//   * Helper methods that creates a GuaranteePolicy object from the attributes
//   * passed in.
//   *
//   * @param someAttributes The attributes from the <CancelPolicy> element.
//   * @return A cancel policy object.
//   */
//  protected CancelPolicy createCancelPolicy(Attributes someAttributes) {
//    CancelPolicy aCancelPolicy = null;
//
//    // Extract the attribute's values
//    String aTimeInterval = someAttributes.getValue(ATTRIBUTE_CANCEL_TIME_INTERVAL);
//    String anIntervalUnits = someAttributes.getValue(ATTRIBUTE_CANCEL_INTERVAL_UNITS);
//    String aCancelTime = someAttributes.getValue(ATTRIBUTE_CANCEL_TIME);
//    String aDescription = someAttributes.getValue(ATTRIBUTE_CANCEL_DESCRIPTION);
//
//    // Create a cancel policy object
//    if (aCancelTime != null && aCancelTime.length() > 0) {
//      /** @todo Change this hack because it returns an ISO time hh:mm:ss.sss */
//      aCancelPolicy = new CancelPolicy(parseISOTime(new ISODate().toISODate()+"T"+aCancelTime+"Z"));
//    } else {
//      int aTimeIntervalInt = 0;
//      if (aTimeInterval != null && aTimeInterval.length() > 0 ) {
//        aTimeIntervalInt = parseInt(aTimeInterval);
//      }
//      aCancelPolicy = new CancelPolicy(aTimeIntervalInt, anIntervalUnits);
//    }
//
//    // Setting the description
//    if (aDescription != null && aDescription.length() > 0) {
//      aCancelPolicy.setDescription(aDescription);
//    }
//
//    return aCancelPolicy;
//  }
//
//  /**
//   * Helper methods that creates a Media object from the attributes
//   * passed in.
//   *
//   * @param someAttributes The attributes from the <Media> element.
//   * @return A media object.
//   */
//  protected Media createMedia(Attributes someAttributes) {
//    // Extract and create Media
//    return new Media(someAttributes.getValue(ATTRIBUTE_MEDIA_DESCRIPTION),
//                     someAttributes.getValue(ATTRIBUTE_MEDIA_LINK),
//                     someAttributes.getValue(ATTRIBUTE_MEDIA_TYPE),
//                     someAttributes.getValue(ATTRIBUTE_MEDIA_MIME),
//                     someAttributes.getValue(ATTRIBUTE_MEDIA_NAME));
//  }
//
//  /**
//   * Helper methods that creates a PhoneNumber object from the attributes
//   * passed in.
//   *
//   * @param someAttributes The attributes from the <Phone> element.
//   * @return A phone number object.
//   */
//  protected PhoneNumber createPhoneNumber(Attributes someAttributes) {
//    // Extract the attribute's values
//    String aNumber = someAttributes.getValue(ATTRIBUTE_PHONE_NUMBER);
//    String aPhoneType = someAttributes.getValue(ATTRIBUTE_PHONE_TYPE);
//
//    // Create a phone number object
//    PhoneNumber aPhoneNumber = new PhoneNumber(aNumber);
//
//    // Setting the phone type
//    if (aNumber != null && aNumber.length() > 0) {
//      aPhoneNumber.setType(aPhoneType);
//    }
//
//    return aPhoneNumber;
//  }
//
//
//  /**
//   * Helper methods that creates a PostalAddress object from the attributes
//   * passed in.
//   *
//   * @param someAttributes The attributes from the <PostalAddress> element.
//   * @return A postal address object.
//   */
//  protected PostalAddress createPostalAddress(Attributes someAttributes) {
//    // Extract the attribute's values
//    String aLine1 = someAttributes.getValue(ATTRIBUTE_LINE1);
//    String aLine2 = someAttributes.getValue(ATTRIBUTE_LINE2);
//    String aLine3 = someAttributes.getValue(ATTRIBUTE_LINE3);
//    String aLine4 = someAttributes.getValue(ATTRIBUTE_LINE4);
//    String aCity = someAttributes.getValue(ATTRIBUTE_CITY);
//    String aStateCode = someAttributes.getValue(ATTRIBUTE_STATE_CODE);
//    String aPostalCode = someAttributes.getValue(ATTRIBUTE_POSTAL_CODE);
//    String aCountryCode = someAttributes.getValue(ATTRIBUTE_COUNTRY_CODE);
//    String aPostalAddressType = someAttributes.getValue(ATTRIBUTE_POSTAL_ADDRESS_TYPE);
//
//    // Create a postal address object
//    PostalAddress aPostalAddress = new PostalAddress(aCity, aStateCode, aPostalCode, aCountryCode);
//
//    // Adding the line 1
//    if (aLine1 != null && aLine1.length() > 0) {
//      aPostalAddress.addAddressLine(aLine1);
//    }
//
//    // Adding the line 2
//    if (aLine2 != null && aLine2.length() > 0) {
//      aPostalAddress.addAddressLine(aLine2);
//    }
//
//    // Adding the line 3
//    if (aLine3 != null && aLine3.length() > 0) {
//      aPostalAddress.addAddressLine(aLine3);
//    }
//
//    // Adding the line 4
//    if (aLine4 != null && aLine4.length() > 0) {
//      aPostalAddress.addAddressLine(aLine4);
//    }
//
//    // Setting the postal address type
//    if (aPostalAddressType != null && aPostalAddressType.length() > 0) {
//      aPostalAddress.setType(aPostalAddressType);
//    }
//
//    return aPostalAddress;
//  }
//
//  /**
//   * Helper methods that creates a Amenity object from the attributes passed in.
//   *
//   * @param someAttributes The attributes from the <Amenity> element.
//   * @return An amenity object.
//   */
//  protected Amenity createAmenity(Attributes someAttributes) {
//    // Extract the attribute's values
//    String anAmenityCode = someAttributes.getValue(ATTRIBUTE_AMENITY_CODE);
//    String aDescription = someAttributes.getValue(ATTRIBUTE_AMENITY_DESCRIPTION);
//    String isAreaLevel = someAttributes.getValue(ATTRIBUTE_AMENITY_AREA);
//    String isConfirmable = someAttributes.getValue(ATTRIBUTE_AMENITY_CONFIRMABLE);
//    String isExtraCharge = someAttributes.getValue(ATTRIBUTE_AMENITY_EXTRA_CHARGE);
//    String isOnRequest = someAttributes.getValue(ATTRIBUTE_AMENITY_ON_REQUEST);
//    String isPropertyLevel = someAttributes.getValue(ATTRIBUTE_AMENITY_PROPERTY);
//    String isRoomLevel = someAttributes.getValue(ATTRIBUTE_AMENITY_ROOM);
//    String aToken = someAttributes.getValue(ATTRIBUTE_AMENITY_TOKEN);
//
//    // Create an amenity
//    Amenity anAmenity = new Amenity(anAmenityCode);
//
//    // Setting the amenity description
//    if (aDescription != null && aDescription.length() > 0) {
//      anAmenity.setDescription(aDescription);
//    }
//
//    // Setting the isArea indicator
//    if (isAreaLevel != null && isAreaLevel.length() > 0) {
//      anAmenity.setIsAreaLevel(parseBoolean(isAreaLevel));
//    }
//
//    // Setting the isConfirmable indicator
//    if (isConfirmable != null && isConfirmable.length() > 0) {
//      anAmenity.setIsConfimable(parseBoolean(isConfirmable));
//    }
//
//    // Setting the isChargeable indicator
//    if (isExtraCharge != null && isExtraCharge.length() > 0) {
//      anAmenity.setIsChargeable(parseBoolean(isExtraCharge));
//    }
//
//    // Setting the isOnRequest indicator
//    if (isOnRequest != null && isOnRequest.length() > 0) {
//      anAmenity.setIsOnRequest(parseBoolean(isOnRequest));
//    }
//
//    // Setting the isPropertyLevel indicator
//    if (isPropertyLevel != null && isPropertyLevel.length() > 0) {
//      anAmenity.setIsPropertyLevel(parseBoolean(isPropertyLevel));
//    }
//
//    // Setting the isRoomLevel indicator
//    if (isRoomLevel != null && isRoomLevel.length() > 0) {
//      anAmenity.setIsRoomLevel(parseBoolean(isRoomLevel));
//    }
//
//    // Setting the amenity token
//    if (aToken != null && aToken.length() > 0) {
//      anAmenity.setToken(aToken);
//    }
//
//    return anAmenity;
//  }
//
//  /**
//   * Helper methods that creates a BookingRequirement object from the attributes passed in.
//   *
//   * @param someAttributes The attributes from the <BookingRequirement> element.
//   * @return A flyer info object.
//   */
//  protected BookingRequirement createBookingRequirement(Attributes someAttributes) {
//    // Extract the attribute's values
//    String aMaximumStay = someAttributes.getValue(ATTRIBUTE_MAXIMUM_STAY);
//    String aMinimumStay = someAttributes.getValue(ATTRIBUTE_MINIMUM_STAY);
//    String isMinimumStayRequired = someAttributes.getValue(ATTRIBUTE_MINIMUM_STAY_REQUIRED);
//    String aBookingTime = someAttributes.getValue(ATTRIBUTE_BOOKING_TIME);
//    String aBookingTimeInterval = someAttributes.getValue(ATTRIBUTE_BOOKING_TIME_INTERVAL);
//    String anIntervalUnits = someAttributes.getValue(ATTRIBUTE_INTERVAL_UNITS);
//
//    // Create a rate plan
//    BookingRequirement aBookingRequirement = new BookingRequirement();
//
//    // Setting the maximum stay
//    if (aMaximumStay != null && aMaximumStay.length() > 0) {
//      aBookingRequirement.setMaximumStay(parseInt(aMaximumStay));
//    }
//
//    // Setting the minimum stay
//    if (aMinimumStay != null && aMinimumStay.length() > 0) {
//      aBookingRequirement.setMinimumStay(parseInt(aMinimumStay));
//    }
//
//    // Setting the is minimum stay indicator
//    if (isMinimumStayRequired != null && isMinimumStayRequired.length() > 0) {
//      aBookingRequirement.setIsMinimumStayRequired(parseBoolean(isMinimumStayRequired));
//    }
//
//    // Setting the booking time
//    if (aBookingTime != null && aBookingTime.length() > 0) {
//      aBookingRequirement.setReservationTime(parseISOTime(new ISODate().toISODate()+"T"+aBookingTime+"Z"));
//
//    }
//
//    // Setting the booking time interval
//    if (aBookingTimeInterval != null && aBookingTimeInterval.length() > 0) {
//      aBookingRequirement.setTimeInterval(parseInt(aBookingTimeInterval));
//    }
//
//    // Setting the interval units
//    if (anIntervalUnits != null && anIntervalUnits.length() > 0) {
//      aBookingRequirement.setIntervalUnits(anIntervalUnits);
//    }
//
//    return aBookingRequirement;
//  }
//
//  /**
//   * Helper methods that creates a Brand object from the attributes
//   * passed in.
//   *
//   * @param someAttributes The attributes from the <Brand> element.
//   * @return A Brand object.
//   */
//  protected PriceRange createPriceRange(Attributes someAttributes) {
//    // Extract the attribute's values
//    String aCurrencyCode = someAttributes.getValue(ATTRIBUTE_CURRENCY);
//    String aMinimumPrice = someAttributes.getValue(ATTRIBUTE_MINIMUM_PRICE);
//    String aMaximumPrice = someAttributes.getValue(ATTRIBUTE_MAXIMUM_PRICE);
//
//    // Create a price range object
//    PriceRange aPriceRange = new PriceRange(aCurrencyCode,
//            parseDouble(aMinimumPrice), parseDouble(aMaximumPrice));
//
//    return aPriceRange;
//  }
//
//  /**
//   * Parses the time value passed in into a Date object.
//   *
//   * @param aTimeValue The time value in the ISO format (yyyy-MM-ddTHH:MM:SSZ).
//   * @return The date object represented by the time value.
//   * @return IllegalArgumentException If the time value passed in is invalid.
//   */
//  protected ISODate parseISOTime(String aTimeValue) {
//    try {
//      return ISODate.parseISODateTime(aTimeValue);
//    } catch (DateParsingException dpe) {
//      String aMessage = "The time value [" + aTimeValue + "] is invalid.";
//      _theLogger.error(dpe, aMessage);
//      throw new IllegalArgumentException(aMessage);
//    }
//  }
//
//  /**
//   * Parses the date value passed in into a Date object.
//   *
//   * @param aDateValue The date value in the ISO format (yyyy-MM-dd).
//   * @return The date object represented by the time value.
//   * @return IllegalArgumentException If the date value passed in is invalid.
//   */
//  protected ISODate parseISODate(String aDateValue) {
//    try {
//      return ISODate.parseISODate(aDateValue);
//    } catch (DateParsingException dpe) {
//      String aMessage = "The date value [" + aDateValue + "] is invalid.";
//      _theLogger.error(dpe, aMessage);
//      throw new IllegalArgumentException(aMessage);
//    }
//  }
//
//  /**
//   * Parses the int value passed in into a int data type.
//   *
//   * @param anIntValue The int value to format.
//   * @return The int that represents the int value.
//   * @return IllegalArgumentException If the int value passed in is invalid.
//   */
//  protected int parseInt(String anIntValue) {
//    try {
//      return Integer.parseInt(anIntValue);
//    } catch (NumberFormatException nfe) {
//      String aMessage = "The int value [" + anIntValue + "] is invalid.";
//      _theLogger.error(nfe, aMessage);
//      throw new IllegalArgumentException(aMessage);
//    }
//  }
//
//  /**
//   * Parses the long value passed in into a long data type.
//   *
//   * @param aLongValue The long value to format.
//   * @return The long that represents the long value.
//   * @return IllegalArgumentException If the long value passed in is invalid.
//   */
//  protected long parseLong(String aLongValue) {
//    try {
//      return Long.parseLong(aLongValue);
//    } catch (NumberFormatException nfe) {
//      String aMessage = "The long value [" + aLongValue + "] is invalid.";
//      _theLogger.error(nfe, aMessage);
//      throw new IllegalArgumentException(aMessage);
//    }
//  }
//
//  /**
//   * Parses the double value passed in into a double data type.
//   *
//   * @param aDoubleValue The double value to format.
//   * @return The dpuble that represents the double value.
//   * @return IllegalArgumentException If the double value passed in is invalid.
//   */
//  protected double parseDouble(String aDoubleValue) {
//    try {
//      return Double.parseDouble(aDoubleValue);
//    } catch (NumberFormatException nfe) {
//      String aMessage = "The double value [" + aDoubleValue + "] is invalid.";
//      _theLogger.error(nfe, aMessage);
//      throw new IllegalArgumentException(aMessage);
//    }
//  }
//
//  /**
//   * Parses the amount value passed in into a double data type.
//   *
//   * @param anAmountValue The amount value to format.
//   * @return The double that represents the amount value.
//   * @return IllegalArgumentException If the amount value passed in is invalid.
//   */
//  protected double parseAmount(String anAmountValue) {
//    try {
//      return _theAmountFormatter.parse(anAmountValue).doubleValue();
//    } catch (ParseException pe) {
//      String aMessage = "The amount value [" + anAmountValue + "] is invalid.";
//      _theLogger.error(pe, aMessage);
//      throw new IllegalArgumentException(aMessage);
//    }
//  }
//
//  /**
//   * Parses the boolean value passed in into a boolean data type.
//   *
//   * @param aBooleanValue The boolean value to format (true, false, 1, 0)
//   * @return The boolean that represents the boolean value.
//   * @return IllegalArgumentException If the boolean value passed in is invalid.
//   */
//  protected boolean parseBoolean(String aBooleanValue) {
//    if (aBooleanValue.equals("true") || aBooleanValue.equals("1")) {
//      return true;
//    } else if (aBooleanValue.equals("false") || aBooleanValue.equals("0")) {
//      return false;
//    } else {
//      throw new IllegalArgumentException("The boolean value [" + aBooleanValue + "] is invalid.");
//    }
//  }
//
//  /////////////////////////////////////////////////////////////////////////////////////////
//  ///////////////////////////////  INTERACE IMPLEMENTATION  ///////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////
//
//  /**
//   * Receives the notification of the the start of an element.
//   *
//   * @param aContext The handler context.
//   * @param anUri The namespace URI associated with the element
//   * @param aLocalName The element type local name.
//   * @param aQualifiedName The element type qualified name.
//   * @param someAttributes The specified or defaulted attributes.
//   * @exception SAXException If an exception occurs.
//   */
//  public void startElement(HandlerContextIF aContext, String anUri, String aLocalName,
//          String aQualifiedName, Attributes someAttributes) throws SAXException {
//  }
//
//  /**
//   * Receives the notification of the the end of an element.
//   *
//   * @param aContext The handler context.
//   * @param anUri The namespace URI associated with the element
//   * @param aLocalName The element type local name.
//   * @param aQualifiedName The element type qualified name.
//   * @exception SAXException If an exception occurs.
//   */
//  public void endElement(HandlerContextIF aContext, String anUri, String aLocalName,
//          String aQualifiedName) throws SAXException {
//  }
//
//  /**
//   * Receives the notification of character data inside an element.
//   *
//   * @param aContext The handler context.
//   * @param someChars The characters.
//   * @param anOffset The start position in the character array.
//   * @param aLength The number of characters to use from the character array.
//   * @exception SAXException If an exception occurs.
//   */
//  public void characters(HandlerContextIF aContext, char[] someChars, int anOffset,
//          int aLength) throws SAXException {
//  }
//
//  /**
//   * Receives the notification of ignorable whitespace in element content.
//   *
//   * @param aContext The handler context.
//   * @param someChars The whitespace characters.
//   * @param anOffset The start position in the character array.
//   * @param aLength The number of characters to use from the character array.
//   * @exception SAXException If an exception occurs.
//   */
//  public void ignorableWhitespace(HandlerContextIF aContext, char[] someChars,
//          int anOffset, int aLength) throws SAXException {
//  }
//}
//
//
////public class HotelMLHandlerState extends AbstractHotelMLHandlerState {
//
//  /////////////////////////////////////////////////////////////////////////////////////////
//  /////////////////////////////////  INSTANCE ATTRIBUTES  /////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////
//
//  /** Indicates if this handler is parsing an HotelML element or not. */
//  private boolean _isParsingHotelMLElement;
//
//  /** The header object generated from the parsing of the HotelML document. */
//  private HotelMLHeader _theHeader;
//
//  /** The request result object generated from the parsing of the HotelML document. */
//  private RequestResultIF _theRequestResult;
//
//  /** Indicates if this handler is parsing an HotelML element or not. */
//  private boolean _isParsingHeadElement;
//
//  /** The handler state that parses a Head element. */
//  private HeadHandlerState _theHeadHandlerState;
//
//  /** Indicates if this handler is parsing a reservation element or not. */
//  private boolean _isParsingReservationElement;
//
//  /** The handler state that parses a Reservation element. */
//  private ReservationHandlerState _theReservationHandlerState;
//
//  /** Indicates if this handler is parsing a property element or not. */
//  private boolean _isParsingPropertyElement;
//
//  /** The handler state that parses a Reservation element. */
//  private PropertyHandlerState _thePropertyHandlerState;
//
//  /** Indicates if this handler is parsing a property search state element or not. */
//  private boolean _isParsingPropertySearchStateElement;
//
//  /** The handler state that parses a Reservation element. */
//  private PropertySearchStateHandler _thePropertySearchStateHandler;
//
//  /////////////////////////////////////////////////////////////////////////////////////////
//  ////////////////////////////////////  CONSTRUCTORS  /////////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////
//
//  /**
//   * Creates a new HotelMLHandlerState instance with the argument passed in.
//   *
//   * @param aLocale The locale of this handler state.
//   * @param aTargetNamespace The targeted namespace of this handler.
//   */
//  public HotelMLHandlerState(Locale aLocale, String aTargetNamespace) {
//    super(aLocale, aTargetNamespace);
//    _isParsingHotelMLElement = false;
//    _isParsingHeadElement = false;
//    _theHeadHandlerState = new HeadHandlerState(aLocale, aTargetNamespace);
//    _isParsingReservationElement = false;
//    _theReservationHandlerState = new ReservationHandlerState(aLocale, aTargetNamespace);
//    _isParsingPropertyElement = false;
//    _thePropertyHandlerState = new PropertyHandlerState(aLocale, aTargetNamespace);
//    _isParsingPropertySearchStateElement = false;
//    _thePropertySearchStateHandler = new PropertySearchStateHandler(aLocale, aTargetNamespace);
//  }
//
//  /////////////////////////////////////////////////////////////////////////////////////////
//  //////////////////////////////////  ACCESSOR METHODS  ///////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////
//
//  /**
//   * Returns the header object generated by parsing the HotelML document.
//   *
//   * @return The header object generated by parsing the HotelML document.
//   * @exception IllegalStateException If the parsing of the HotelML element is not done.
//   */
//  public HotelMLHeader getHeader() {
//    if (_isParsingHotelMLElement) {
//      throw new IllegalStateException("Parsing an <HotelML> element.");
//    }
//
//    return _theHeader;
//  }
//
//  /**
//   * Returns the request result object generated by parsing the HotelML document.
//   *
//   * @return The request result object generated by parsing the HotelML document.
//   * @exception IllegalStateException If the parsing of the HotelML element is not done.
//   */
//  public RequestResultIF getRequestResult() {
//    if (_isParsingHotelMLElement) {
//      throw new IllegalStateException("Parsing an <HotelML> element.");
//    }
//
//    return _theRequestResult;
//  }
//
//  /////////////////////////////////////////////////////////////////////////////////////////
//  //////////////////////////////////  OVERRIDEN METHODS  //////////////////////////////////
//  /////////////////////////////////////////////////////////////////////////////////////////
//
//  /**
//   * Receives the notification of the the start of an element.
//   *
//   * @param aContext The handler context.
//   * @param anUri The namespace URI associated with the element
//   * @param aLocalName The element type local name.
//   * @param aQualifiedName The element type qualified name.
//   * @param someAttributes The specified or defaulted attributes.
//   * @exception SAXException If an exception occurs.
//   */
//  public void startElement(HandlerContextIF aContext, String anUri, String aLocalName,
//          String aQualifiedName, Attributes someAttributes) throws SAXException {
//    // Verify that it's an HotelML element
//    if (anUri.equals(getTargetNamespace())) {
//      // If we start a head element
//      if (aLocalName.equals(ELEMENT_HOTELML)) {
//        _isParsingHotelMLElement = true;
//      }
//
//      // If we start a head element
//      else if (_isParsingHotelMLElement && aLocalName.equals(ELEMENT_HEAD)) {
//        _isParsingHeadElement = true;
//
//        // Delegating the parsing of the head to a HeadHandlerState
//        aContext.setCurrentState(_theHeadHandlerState, anUri, aLocalName,
//                  aQualifiedName, someAttributes);
//      }
//
//      // If we start a reservation element
//      else if (_isParsingHotelMLElement && aLocalName.equals(ELEMENT_RESERVATION)) {
//        _isParsingReservationElement = true;
//
//        // Delegating the parsing of the reservation to a ReservationHandlerState
//        aContext.setCurrentState(_theReservationHandlerState, anUri, aLocalName,
//                aQualifiedName, someAttributes);
//      }
//
//      // If we start a property element
//      else if (_isParsingHotelMLElement && aLocalName.equals(ELEMENT_PROPERTY)) {
//        _isParsingPropertyElement = true;
//
//        // Delegating the parsing of the property to a ReservationHandlerState
//        aContext.setCurrentState(_thePropertyHandlerState, anUri, aLocalName,
//                aQualifiedName, someAttributes);
//      }
//
//      // If we start a property search state element
//      else if (_isParsingHotelMLElement && aLocalName.equals(ELEMENT_PROPERTY_SEARCH_STATE)) {
//        _isParsingPropertySearchStateElement = true;
//
//        // Delegating the parsing of the property to a ReservationHandlerState
//        aContext.setCurrentState(_thePropertySearchStateHandler, anUri, aLocalName,
//                aQualifiedName, someAttributes);
//      }
//    }
//  }
//
//  /**
//   * Receives the notification of the the end of an element.
//   *
//   * @param aContext The handler context.
//   * @param anUri The namespace URI associated with the element
//   * @param aLocalName The element type local name.
//   * @param aQualifiedName The element type qualified name.
//   * @exception SAXException If an exception occurs.
//   */
//  public void endElement(HandlerContextIF aContext, String anUri, String aLocalName,
//          String aQualifiedName) throws SAXException {
//    // Verify that it's an HotelML element
//    if (anUri.equals(getTargetNamespace())) {
//      // If we close a reservation element
//      if (aLocalName.equals(ELEMENT_RESERVATION)) {
//        _isParsingReservationElement = false;
//
//        // Setting the reservation result
//        _theRequestResult = _theReservationHandlerState.getReservationResult();
//      }
//
//      // If we close a property element
//      else if (aLocalName.equals(ELEMENT_PROPERTY)) {
//        _isParsingPropertyElement = false;
//
//        if (_theRequestResult == null) {
//          _theRequestResult = new PropertyInformationResult();
//        }
//
//        // Adding the property to the property result
//        ((PropertyInformationResult) _theRequestResult).
//                addProperty(_thePropertyHandlerState.getProperty());
//      }
//
//      // If we close a property search state element
//      else if (aLocalName.equals(ELEMENT_PROPERTY_SEARCH_STATE)) {
//        _isParsingPropertySearchStateElement = false;
//
//        // Setting the property search result
//        _theRequestResult = _thePropertySearchStateHandler.getPropertySearchResult();
//      }
//
//      // If we close a head element
//      else if (aLocalName.equals(ELEMENT_HEAD)) {
//        _isParsingHeadElement = false;
//
//        // Setting the header
//        _theHeader = _theHeadHandlerState.getHeader();
//      }
//
//      // If we close an hotelml element
//      else if (aLocalName.equals(ELEMENT_HOTELML)) {
//        // If no content was found create an erronous result
//        if (_theRequestResult == null) {
//          _theRequestResult = new ErronousResult();
//        }
//
//        _isParsingHotelMLElement = false;
//      }
//    }
//  }
//}
