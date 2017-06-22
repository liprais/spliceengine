/*
 * Copyright (c) 2012 - 2017 Splice Machine, Inc.
 *
 * This file is part of Splice Machine.
 * Splice Machine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either
 * version 3, or (at your option) any later version.
 * Splice Machine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License along with Splice Machine.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.splicemachine.spark.splicemachine

import org.apache.spark.sql.execution.datasources.jdbc.JDBCOptions
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import scala.util.Random




@RunWith(classOf[JUnitRunner])
class SupplyChainSimulator extends Timeline  {


	//Utility Class for Random Number generation
	object RandomGen {
	  private var random = new util.Random
	
	  // Returns a random integer in range [0,range-1]
	  def rand(range: Int): Int = random.nextInt(range)
	
	  // Returns a random integer in range [first,last]
	  def randBetween(first: Int, last: Int): Int = first + rand(last-first+1)
	
	  // Create a new random number generator with the given Int "seed".
	  def setRandSeed(seed: Int)  { random = new Random(seed) }
	 
	}
	/**
		*Baseclass for all Order Events
		* The behaviors of specific Event subtypes must be defined implementation for method processEvent.
		*
		* @param epart : Part ID of the order event
		* @param eorderDate : Shipping Date as a string
		* @param edeliveryDate : Delivery Date as a String
		* @param eqty : Quantity of the order event
		*/

	abstract class Event(epart: Integer,
											 eorderDate: String,
											 edeliveryDate: String,
											 eqty: Long)  {
		def processEvent

		def part: Int = epart
		def orderDate: String = eorderDate
		def deliveryDate: String = edeliveryDate
		def qty: Long = eqty
	}

	/** Simulation Driver Class
		* Provides the Event Queue and method to process the Queue
		*/
	class Simulation() {

		var eventQueue = new collection.mutable.Queue[Event]  //  events queue

		// Add newEvent to  events queue
		def scheduleEvent(newEvent: Event) {
			eventQueue.enqueue(newEvent)
		}
		//  method for simulation of events
		final def run {
			//println("START SIMULATE RUN ")
			while (!eventQueue.isEmpty) {        // while more events to process
			val nextEvent = eventQueue.dequeue //   get  event
				nextEvent.processEvent             //   execute the event
			}
		}
	}


	/*******
		* PURCHASE ORDER EVENTS *
		********/

	/** Event to Create Purchase Order
		*
		* @param part : Part ID of the order
		* @param orderDate : Shipping Date as a string
		* @param deliveryDate : Delivery Date as a String
		* @param qty : Quantity of the order
		*/

	class POCreateEvent(part: Int, orderDate: String, deliveryDate: String, qty: Long ) extends Event(part, orderDate, deliveryDate, qty) {
		def processEvent {
			println( this.toString )
			PurchaseOrder.create(part, orderDate, deliveryDate, qty)
		}

		override def toString = "POCreateEvent(" + part + "," + orderDate  + "," + deliveryDate  + "," + qty  + ")"
	}

	/** Event  to Change Purchase Order Quantity
		*
		* @param part : Part ID of the order
		* @param orderDate : Shipping Date as a string
		* @param deliveryDate : Delivery Date as a String
		* @param qty : Original Quantity of the order
		* @param newQty : New Quantity to set
		*/
	class POChangeQtyEvent(part: Int, orderDate: String, deliveryDate: String, qty: Long, newQty: Long )  extends Event(part, orderDate, deliveryDate, qty) {
		def processEvent {
			println( this.toString )
			PurchaseOrder.changeQty(part, orderDate, deliveryDate, qty, newQty)

		}

		override def toString = "POChangeQty(" + part + "," + orderDate  + "," + deliveryDate  + "," + qty  +"," + newQty +  ")"
	}

	/** Event  to Change Purchase Order Delivery Date
		*
		* @param part : Part ID of the order
		* @param orderDate : Shipping Date as a string
		* @param deliveryDate : Original Delivery Date as a String
		* @param newDeliveryDate: New Delivery Date as a String
		* @param qty :  Quantity of the order
		*
		*/
	class POChangeDeliveryEvent(part: Int, orderDate: String, deliveryDate: String, newDeliveryDate: String,  qty: Long )  extends Event(part, orderDate, deliveryDate, qty) {
		def processEvent {
			println( this.toString )
			PurchaseOrder.changeDelivery(part, orderDate, deliveryDate,newDeliveryDate, qty)

		}

		override def toString = "POChangeDelivery(" + part + "," + orderDate  + "," + deliveryDate  + "," + newDeliveryDate + ","  + qty  + ")"
	}



	/*******
		* SALES ORDER EVENTS *
		********/
	/** Event to Create Sales Order
		*
		* @param part : Part ID of the order
		* @param orderDate : Shipping Date as a string
		* @param deliveryDate : Delivery Date as a String
		* @param qty : Quantity of the order
		*/
	class SOCreateEvent(part: Int, orderDate: String, deliveryDate: String, qty: Long ) extends Event(part, orderDate, deliveryDate, qty) {
		def processEvent {
			println( this.toString )
			SalesOrder.create(part, orderDate, deliveryDate, qty)
		}

		override def toString = "SOCreateEvent(" + part + "," + orderDate  + "," + deliveryDate  + "," + qty  + ")"
	}

	/** Event to Change Sales Order Quantity
		*
		* @param part : Part ID of the order
		* @param orderDate : Shipping Date as a string
		* @param deliveryDate : Delivery Date as a String
		* @param qty : Original Quantity of the order
		* @param newQty : New Quantity to set
		*/
	class SOChangeQtyEvent(part: Int, orderDate: String, deliveryDate: String, qty: Long, newQty: Long )  extends Event(part, orderDate, deliveryDate, qty) {
		def processEvent {
			println( this.toString )
			SalesOrder.changeQty(part, orderDate, deliveryDate, qty, newQty)
		}

		override def toString = "SOChangeQty(" + part + "," + orderDate  + "," + deliveryDate  + "," + qty  +"," +newQty +  ")"
	}

	/** Event  to Change Sales Order Delivery Date
		*
		* @param part : Part ID of the order
		* @param orderDate : Shipping Date as a string
		* @param deliveryDate : Original Delivery Date as a String
		* @param newDeliveryDate: New Delivery Date as a String
		* @param qty :  Quantity of the order
		*
		*/
	class SOChangeDeliveryEvent(part: Int, orderDate: String, deliveryDate: String, newDeliveryDate: String,  qty: Long )  extends Event(part, orderDate, deliveryDate, qty) {
		def processEvent {
			println( this.toString )
			SalesOrder.changeDelivery(part, orderDate, deliveryDate,newDeliveryDate, qty)
		}
		override def toString = "SOChangeDelivery(" + part + "," + orderDate  + "," + deliveryDate  + "," + newDeliveryDate + ","  + qty  + ")"
	}


	/*******
		* TRANSFER ORDER EVENTS *
		********/
	/** Event to Create Tranfer Order
		*
		* @param srcPart : Original Part ID of the order
		* @param destPart : New Part ID of the order
		* @param orderDate : Shipping Date as a string
		* @param deliveryDate : Delivery Date as a String
		* @param qty : Quantity of the order
		*/
	class TOCreateEvent (srcPart: Int, destPart: Int, orderDate: String, deliveryDate: String, qty: Long ) extends Event(srcPart, orderDate, deliveryDate, qty) {
		def processEvent {
			println( this.toString )
			TransferOrder.createNoSave(srcPart, destPart, orderDate, deliveryDate, qty)

		}

		override def toString = "TOCreateEvent(" +srcPart + "," + destPart + "," + orderDate  + "," + deliveryDate  + "," + qty  + ")"
	}

	/**Event to Change Transfer Order Delivery Date
		*\
		*
		* @param srcPart : Original Part ID of the order
		* @param destPart : New Part ID of the order
		* @param orderDate : Shipping Date as a string
		* @param deliveryDate : Delivery Date as a String
		* @param newDeliveryDate: New Delivery Date as a String
		* @param qty : Quantity of the order
		*
		*/
	class TOChangeDeliveryEvent(srcPart: Int, destPart: Int, orderDate: String, deliveryDate: String, newDeliveryDate: String,  qty: Long )  extends Event(srcPart, orderDate, deliveryDate, qty) {
		def processEvent {
			println( this.toString )
			TransferOrder.changeDeliveryNoSave(srcPart, destPart, orderDate, deliveryDate, newDeliveryDate, qty)
		}
		override def toString = "TOChangeDelivery(" + srcPart + "," + destPart + "," + orderDate  + "," + deliveryDate  + "," + newDeliveryDate + ","  + qty  + ")"
	}

	/**Event to Change Transfer Order Quantity
		*
		* @param srcPart : Original Part ID of the order
		* @param destPart : New Part ID of the order
		* @param orderDate : Shipping Date as a string
		* @param deliveryDate : Delivery Date as a String
		* @param qty : Quantity of the order
		* @param newQty : New Quantity of the order
		*/
	class TOChangeQtyEvent (srcPart: Int, destPart: Int, orderDate: String, deliveryDate: String,  qty: Long,  newQty: Long )  extends Event(srcPart, orderDate, deliveryDate, qty) {
		def processEvent {
			println( this.toString )
			TransferOrder.changeQty(srcPart, destPart, orderDate, deliveryDate, qty, newQty)
		}

		override def toString = "TOChangeQtyEvent(" +srcPart + "," + destPart + "," + orderDate  + "," + deliveryDate  + "," + qty  + "," + newQty +")"
	}



	class TOObjectCreateEvent (psource: Int, pdestination: Int, pshippingDate: String, pdeliveryDate: String, pqty: Long,
														 pTO_Id: Integer,
														 psupplier: String,
														 pASN: String,
														 pcontainer: String,
														 pmodeOfTransport: Integer,
														 pcarrier: Integer,
														 pfromWeather: Integer,
														 ptoWeather: Integer,
														 platitude: Double,
														 plongitude: Double,
														 psourceCity: Integer,
														 pdestinationCity: Integer,
														 pPO_Id: Integer,
														 pmodDeliveryDate: String) extends Event(psource, pshippingDate, pdeliveryDate, pqty) {
		def processEvent {
			//println("START TOObj Create Event")
			println( this.toString )
			TransferOrder.create(part, destination, orderDate, deliveryDate, modDeliveryDate, qty,0,
				TO_Id, supplier, ASN, container, modeOfTransport, carrier,fromWeather, toWeather, latitude,longitude,sourceCity,destinationCity, PO_Id)

		}

		override def toString = "TOObjectCreateEvent(" +part + "," + destination + "," + orderDate  + "," + deliveryDate  + "," + qty  + ")"


		def destination: Int = pdestination
		def modDeliveryDate : String = pmodDeliveryDate
		def TO_Id: Integer = pTO_Id
		def supplier: String = psupplier
		def ASN: String = pASN
		def container: String = pcontainer
		def modeOfTransport: Integer = pmodeOfTransport
		def carrier: Integer = pcarrier
		def fromWeather: Integer = pfromWeather
		def toWeather: Integer = ptoWeather
		def latitude: Double = platitude
		def longitude: Double = plongitude
		def sourceCity: Integer = psourceCity
		def destinationCity: Integer = pdestinationCity
		def PO_Id: Integer = pPO_Id



	}

	class TOObjectChangeDeliveryEvent (source: Int, destination: Int, shippingDate: String, deliveryDate: String, newDeliveryDate: String, qty: Long,
														 TO_Id: Integer,
														 supplier: String,
														  modeOfTransport: Integer,
														 carrier: Integer,
														 fromWeather: Integer,
														 toWeather: Integer,
														 sourceCity: Integer,
														 destinationCity: Integer,
																		 TO_event_Id: Integer) extends Event(source, shippingDate, deliveryDate, qty) {
		def processEvent {
			println("START TOObj Change Event")
			println( this.toString )
			TransferOrder.changeDelivery(source, destination, shippingDate, deliveryDate, newDeliveryDate, qty, 0, TO_Id,
			supplier, modeOfTransport, carrier, fromWeather, toWeather, sourceCity, destinationCity, TO_event_Id)

		}

		override def toString = "TOObjectChangeDeliveryEvent(" +source + "," + destination + "," + shippingDate  + "," + deliveryDate  + "," + qty  + ")"
	}
	/**Test Event
		*
		*/

	class TestEvent (srcPart: Int, destPart: Int, orderDate: String, deliveryDate: String,  qty: Long,  newQty: Long )  extends Event(srcPart, orderDate, deliveryDate, qty) {
		def processEvent {
			println( this.toString )
			// TransferOrder.changeQty(srcPart, destPart, orderDate, deliveryDate, qty, newQty)
		}

		override def toString = "TestEvent(" +srcPart + "," + destPart + "," + orderDate  + "," + deliveryDate  + "," + qty  + "," + newQty +")"
	}



	/**Simulator of Order Events
		*
		*
		*/

	object SupplyChain {

		val fmt   = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

		val BEGIN_ORDER_DATE  ="2017-7-05 00:00:00"    // beginning of simulation
		val TOTAL_TICKS     = 10  // Number of ticks in simulation
		val TICK_LENGTH_DAYS  = 1 // Lenght in days between each Tick in realtime
		val TICKINTERVAL  =10 // UI interval between ticks

		val MIN_ENDDAYS    = 1    // minimum delivery duration from orderdate
		val MAX_ENDDAYS    = 7   // maximum delivery duration fro orderdate


		val MIN_OID    = 1    // minimum order qty
		val MAX_OID   = 999999999   // maximum order qty


		val MIN_QTY    = 1    // minimum order qty
		val MAX_QTY    = 1000   // maximum order qty

		val MIN_ASN = 1
		val MAX_ASN= 999999999

		val MIN_CNT = 1
		val MAX_CNT = 999999999

		val parts = Array(100, 200, 300, 400, 500)
		val partsCnt = parts.length

		val suppliers = Array("A", "B", "C", "D", "E")
		val suppliersCnt = suppliers.length

		val modesOfTransport = Array (0,1,2) //Ground, Air, Sea
		val carriers = Array(1,2,3,4)  // UPS, FEDEX, USPS, DHL
		val weatherList = Array (0,1,2,3)  // Clear, Lt Precipitation, Heavy Rain, Heavy Snow




		val theSimulation = new Simulation()

		def simulate (pTotalTicks :Int, pEventTypes :String, pInit :String) {
			//println("START SIMULATE")
			var totalTicks = pTotalTicks
			if(totalTicks == 0)
				totalTicks = TOTAL_TICKS

			var eventTypesStr = pEventTypes
			if(eventTypesStr.length ==0 )
				eventTypesStr= "1,2,3"

			//println("EVENT TYPES :" + eventTypesStr)
			var eventTypesList = eventTypesStr.split(",")


			//First load intial Inventory
			if(pInit.equals("1")) {
				//println("CLEAR DB")
				createTimeline(internalTN)
				createtransferOrderTable(TOTable)
				createTODeliveryChgEventTable(TODelviraryChgEventTable)
				for (part <- parts) {
					Inventory.create(part)
				}
			}

			// load queue with some number of events
			var t = 0
			var curOrderDate = org.joda.time.DateTime.parse(BEGIN_ORDER_DATE, fmt)

			//println("START Creating events")
			while (t < totalTicks) {
				//Purchase Order Create Events
				if( eventTypesList contains "1"){
					for (part <- parts) {
						if ( RandomGen.rand(100) >25) {
							theSimulation.scheduleEvent(
								new POCreateEvent(part,  curOrderDate.toString(fmt), (curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt),
									RandomGen.randBetween(MIN_QTY,MAX_QTY)) )

						}
					}
				}

				//Purchase Order Change Qty
				if( eventTypesList contains "2"){
					for (part <- parts) {
						if ( RandomGen.rand(100) >75) {
							theSimulation.scheduleEvent(
								new POChangeQtyEvent(part,  curOrderDate.toString(fmt), (curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt),
									RandomGen.randBetween(MIN_QTY,MAX_QTY),  RandomGen.randBetween(MIN_QTY,MAX_QTY)) )

						}
					}
				}
				//Purchase Order DeliveryDate Qty
				if( eventTypesList contains "3"){
					for (part <- parts) {
						if ( RandomGen.rand(100) >75) {
							theSimulation.scheduleEvent(
								new POChangeDeliveryEvent(part,  curOrderDate.toString(fmt), (curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt),
									(curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt),
									RandomGen.randBetween(MIN_QTY,MAX_QTY)) )

						}
					}
				}

				//Sales ORder Create Events
				if( eventTypesList contains "4"){
					for (part <- parts) {
						if ( RandomGen.rand(100) >25) {
							theSimulation.scheduleEvent(
								new SOCreateEvent(part,  curOrderDate.toString(fmt), (curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt),
									RandomGen.randBetween(MIN_QTY,MAX_QTY)) )

						}
					}
				}

				//Sales Order Change Qty
				if( eventTypesList contains "5"){
					for (part <- parts) {
						if ( RandomGen.rand(100) >75) {
							theSimulation.scheduleEvent(
								new SOChangeQtyEvent(part,  curOrderDate.toString(fmt), (curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt),
									RandomGen.randBetween(MIN_QTY,MAX_QTY),  RandomGen.randBetween(MIN_QTY,MAX_QTY)) )

						}
					}
				}

				//Sales Order DeliveryDate Qty
				if( eventTypesList contains "6"){
					for (part <- parts) {
						if ( RandomGen.rand(100) >75) {
							theSimulation.scheduleEvent(
								new SOChangeDeliveryEvent(part,  curOrderDate.toString(fmt), (curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt),
									(curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt),
									RandomGen.randBetween(MIN_QTY,MAX_QTY)) )

						}
					}
				}

				//Transfer ORder Create Events
				if( eventTypesList contains "7"){
					for (part <- parts) {
						if ( RandomGen.rand(100) >50) {
							var destination = parts(RandomGen.randBetween(0,partsCnt-1))
							if(part !=destination ) {
								theSimulation.scheduleEvent(
									new TOCreateEvent(part,destination, curOrderDate.toString(fmt), (curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt),
										RandomGen.randBetween(MIN_QTY,MAX_QTY)) )
							}
						}
					}
				}

				//Transfer Order Change Qty
				if( eventTypesList contains "8"){
					for (part <- parts) {
						if ( RandomGen.rand(100) >75) {
							var destination = parts(RandomGen.randBetween(0,partsCnt-1))
							if(part !=destination ) {
								theSimulation.scheduleEvent(
									new TOChangeQtyEvent(part ,destination, curOrderDate.toString(fmt), (curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt),
										RandomGen.randBetween(MIN_QTY,MAX_QTY),  RandomGen.randBetween(MIN_QTY,MAX_QTY)) )
							}
						}
					}
				}

				//Trasfer Order DeliveryDate Qty
				if( eventTypesList contains "9"){
					for (part <- parts) {
						if ( RandomGen.rand(100) >75) {
							var destination = parts(RandomGen.randBetween(0,partsCnt-1))
							if(part !=destination ) {
								theSimulation.scheduleEvent(
									new TOChangeDeliveryEvent(part ,destination, curOrderDate.toString(fmt), (curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt),
										(curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt),   RandomGen.randBetween(MIN_QTY,MAX_QTY)) )
							}
						}
					}
				}

				//Transfer ORder Create Events
				if( eventTypesList contains "10"){
					//println("Creating TOObjectCreate")
					for (part <- parts) {
						if ( RandomGen.rand(100) >25) {
							var destination = parts(RandomGen.randBetween(0,partsCnt-1))
							if(part !=destination ) {
								var snowevent = (RandomGen.randBetween(0,100) > 25 )
								var toObject =generateTOObject(part,destination, curOrderDate,snowevent )
									theSimulation.scheduleEvent(toObject)
								 if(snowevent)
									 theSimulation.scheduleEvent(generateTODeliveryChgEventObject(part,destination, curOrderDate,toObject ))
							}
						}
					}
				}
				/*

				//Transfer ORder Change Delivery  Events
				if( eventTypesList contains "11") {
					println("Creating TOObjectChgDeliver")
					for (part <- parts) {
						if ( RandomGen.rand(100) >25) {
							var destination = parts(RandomGen.randBetween(0,partsCnt-1))
							if(part !=destination ) {
								theSimulation.scheduleEvent(
									generateTODeliveryChgEventObject(part,destination, curOrderDate ))
							}
						}
					}
				}
			*/


				theSimulation.run
				t += 1
				curOrderDate = curOrderDate.plusDays(TICK_LENGTH_DAYS);
				//wait for tick interval
				Thread.sleep(TICKINTERVAL)
			}
		}

		// convenience methods to delegate calls to the Simulation instance
		def scheduleEvent(newEvent: Event) { theSimulation.scheduleEvent(newEvent) }

		def generateTOObject (part : Int, destination :Int, curOrderDate : org.joda.time.DateTime, snowEvent : Boolean) :TOObjectCreateEvent = {

			//println("START GEN")
			var shippingDate = curOrderDate.toString(fmt)
			var deliveryDate = (curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt)
			var qty = RandomGen.randBetween(MIN_QTY,MAX_QTY)
			var toId = RandomGen.randBetween(MIN_OID, MAX_OID)
			var poId = RandomGen.randBetween(MIN_OID, MAX_OID)

			var supplier = suppliers(RandomGen.randBetween(0,suppliers.length-1))
			var asn = RandomGen.randBetween(MIN_ASN,MAX_ASN).toString
			var container = RandomGen.randBetween(MIN_CNT,MAX_CNT).toString

			var carrier = carriers(RandomGen.randBetween(0,carriers.length-1))

			var srcCity = RandomGen.randBetween(0,cities.length-1)
			var destCity  = RandomGen.randBetween(0,cities.length-1)

			var latitude = cities(srcCity).Latitude
			var longitude = cities(srcCity).Latitude
			var modeOfTransport = modesOfTransport(RandomGen.randBetween(0,modesOfTransport.length-1))
			var fromWeather = weatherList(RandomGen.randBetween(0,weatherList.length-1))
			var toWeather = weatherList(RandomGen.randBetween(0,weatherList.length-1))


			if(snowEvent && (RandomGen.randBetween(0, 100) > 10)) {
				// change one of the weather
				val snow = weatherList(weatherList.length - 1)
				val prob = RandomGen.randBetween(0, 100)
				if (prob < 45)
					fromWeather = snow
				else if (prob >= 45 && prob < 90)
					toWeather = snow
				else {
					toWeather = snow
					fromWeather = snow
				}

			}


				new TOObjectCreateEvent(part, destination,shippingDate, deliveryDate, qty, toId, supplier, asn, container,
					modeOfTransport, carrier,fromWeather, toWeather, latitude,longitude, srcCity,destCity, poId, deliveryDate )
		}


		def generateTODeliveryChgEventObject (part : Int, destination :Int, curOrderDate : org.joda.time.DateTime,
																					toObject:TOObjectCreateEvent) :TOObjectChangeDeliveryEvent = {

			println("START GEN")
			var shippingDate = toObject.orderDate
			var deliveryDate = toObject.deliveryDate

			//Add 1 to 10 days to original delivery date
			var newDeliveryDate = org.joda.time.DateTime.parse(deliveryDate, fmt).plusDays(RandomGen.randBetween(1,10)).toString(fmt)
			var qty = toObject.qty
			var toId = toObject.TO_Id
			var TO_event_Id = RandomGen.randBetween(MIN_OID, MAX_OID)

			var supplier = toObject.supplier

			var carrier = toObject.carrier

			var srcCity =toObject.sourceCity
			var destCity  = toObject.destinationCity

			//var latitude = cities(srcCity).Latitude
			//var longitude = cities(srcCity).Latitude
			var modeOfTransport = toObject.modeOfTransport

			//For Change Date Event, 90% of cases should have one of the weather as either
			// heavy snow

			var fromWeather = toObject.fromWeather
			var toWeather = toObject.toWeather


			new TOObjectChangeDeliveryEvent(part, destination,shippingDate, deliveryDate, newDeliveryDate, qty,
				toId, supplier, modeOfTransport, carrier,fromWeather, toWeather, srcCity,destCity, TO_event_Id )
		}

	/*

	def generateTODeliveryChgEventObject (part : Int, destination :Int, curOrderDate : org.joda.time.DateTime) :TOObjectChangeDeliveryEvent = {

		println("START GEN")
		var shippingDate = curOrderDate.toString(fmt)
		var deliveryDate = (curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt)
		var newDeliveryDate = (curOrderDate.plusDays(RandomGen.randBetween(MIN_ENDDAYS,MAX_ENDDAYS))).toString(fmt)
		var qty = RandomGen.randBetween(MIN_QTY,MAX_QTY)
		var toId = RandomGen.randBetween(MIN_OID, MAX_OID)
		var TO_event_Id = RandomGen.randBetween(MIN_OID, MAX_OID)

		var supplier = suppliers(RandomGen.randBetween(0,suppliers.length-1))

		var carrier = carriers(RandomGen.randBetween(0,carriers.length-1))

		var srcCity = RandomGen.randBetween(0,cities.length-1)
		var destCity  = RandomGen.randBetween(0,cities.length-1)

		//var latitude = cities(srcCity).Latitude
		//var longitude = cities(srcCity).Latitude
		var modeOfTransport = modesOfTransport(RandomGen.randBetween(0,modesOfTransport.length-1))

		//For Change Date Event, 90% of cases should have one of the weather as either
		// heavy snow


		var fromWeather = weatherList(RandomGen.randBetween(0,weatherList.length-1))
		var toWeather = weatherList(RandomGen.randBetween(0,weatherList.length-1))


		if(RandomGen.randBetween(0,100) >10 ) {
		// change one of the weather
			val snow = weatherList(weatherList.length-1)
			val  prob = RandomGen.randBetween(0,100)
			if (prob < 45)
				fromWeather =snow
			else if (prob >=45 && prob  < 90)
				toWeather =snow
			else {
				toWeather =snow
				fromWeather =snow
			}

		}

		new TOObjectChangeDeliveryEvent(part, destination,shippingDate, deliveryDate, newDeliveryDate, qty,
			toId, supplier, modeOfTransport, carrier,fromWeather, toWeather, srcCity,destCity, TO_event_Id )
	}
	*/
}

test("Supply Chain Simulator TOObject Change Delivery ") {


		//Generate Purchase Orders for 1 day, after clearing database
		var noOfDays = 200
		var eventTypes = "10"
		var clearDB = "2"


		//println("RUN TEST TO")
		SupplyChain.simulate(noOfDays,eventTypes ,clearDB )
		val optionMap = Map(
			JDBCOptions.JDBC_TABLE_NAME -> TODelviraryChgEventTable,
			JDBCOptions.JDBC_URL -> defaultJDBCURL
		)
		var df = sqlContext.read.options(optionMap).splicemachine
		assert(df.count > 0)
	}


	ignore("Supply Chain Simulator TOObject ") {


		//Generate Purchase Orders for 1 day, after clearing database
		var noOfDays = 1
		var eventTypes = "10"
		var clearDB = "1"


		//println("RUN TEST TO")
		SupplyChain.simulate(noOfDays,eventTypes ,clearDB )
		val optionMap = Map(
			JDBCOptions.JDBC_TABLE_NAME -> TOTable,
			JDBCOptions.JDBC_URL -> defaultJDBCURL
		)
		var df = sqlContext.read.options(optionMap).splicemachine
		assert(df.count > 0)
	}



	test("Supply Chain Simulator  ") {


		//Generate Purchase Orders for 1 day, after clearing database
		var noOfDays = 1
		var eventTypes = "1"
		var clearDB = "1"

		var df = sqlContext.read.options(internalOptions).splicemachine
		assert(df.count > 0)

		//Generate Purchase Orders for 2 days, after clearing database
		noOfDays = 2
		eventTypes = "1"
		clearDB = "1"
		SupplyChain.simulate(noOfDays,eventTypes ,clearDB )
		df = sqlContext.read.options(internalOptions).splicemachine
		assert(df.count > 0)


		//Generate fPurchase Order for 1 day without clearing db

		noOfDays = 1
		eventTypes = "1"
		clearDB = "2"
		SupplyChain.simulate(noOfDays,eventTypes ,clearDB )
		df = sqlContext.read.options(internalOptions).splicemachine
		assert(df.count > 0)

		//Geenrate TransferOrder for 1  day after clearing database
		noOfDays = 1
		eventTypes = "1,7"
		clearDB = "1"
		SupplyChain.simulate(noOfDays,eventTypes ,clearDB )
		df = sqlContext.read.options(internalOptions).splicemachine
		assert(df.count > 0)


	}



}