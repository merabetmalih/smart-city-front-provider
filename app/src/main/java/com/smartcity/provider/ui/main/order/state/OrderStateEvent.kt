package com.smartcity.provider.ui.main.order.state

import com.smartcity.provider.util.StateEvent

sealed class OrderStateEvent: StateEvent {

    class GetOrderEvent : OrderStateEvent() {
        override fun errorInfo(): String {
            return "Get orders attempt failed."
        }

        override fun toString(): String {
            return "GetOrderStateEvent"
        }
    }

    class GetTodayOrderEvent : OrderStateEvent(){
        override fun errorInfo(): String {
            return "Get today orders attempt failed."
        }

        override fun toString(): String {
            return "GetTodayOrderStateEvent"
        }
    }

    class GetOrderByDateEvent() : OrderStateEvent(){
        override fun errorInfo(): String {
            return "Get orders attempt failed."
        }

        override fun toString(): String {
            return "GetOrderByDateStateEvent"
        }
    }

    class SetOrderAcceptedEvent(
        var id:Long
    ) : OrderStateEvent(){
        override fun errorInfo(): String {
            return "Set order accepted failed."
        }

        override fun toString(): String {
            return "SetOrderAcceptedStateEvent"
        }
    }

    class SetOrderRejectedEvent(
        var id:Long
    ) : OrderStateEvent(){
        override fun errorInfo(): String {
            return "Set order rejected failed."
        }

        override fun toString(): String {
            return "SetOrderRejectedStateEvent"
        }
    }

    class SetOrderReadyEvent(
        var id:Long
    ) : OrderStateEvent(){
        override fun errorInfo(): String {
            return "Set order ready failed."
        }

        override fun toString(): String {
            return "SetOrderReadyStateEvent"
        }
    }

    class SetOrderDeliveredEvent(
        var id:Long,
        var comment:String?,
        var date:String?
    ) : OrderStateEvent(){
        override fun errorInfo(): String {
            return "Set order delivered failed."
        }

        override fun toString(): String {
            return "SetOrderDeliveredStateEvent"
        }
    }

    class SetOrderPickedUpEvent(
        var id:Long,
        var comment:String?,
        var date:String?
    ) : OrderStateEvent(){
        override fun errorInfo(): String {
            return "Set order picked up failed."
        }

        override fun toString(): String {
            return "SetOrderPickedUpStateEvent"
        }
    }

    class GetOrderByIdEvent(
        var orderId : Long
    ) : OrderStateEvent(){
        override fun errorInfo(): String {
            return "Get order attempt failed."
        }

        override fun toString(): String {
            return "GetOrderByIdStateEvent"
        }
    }

    class SearchOrderByReceiverEvent(
        var firstName :String,
        var lastName :String
    ) : OrderStateEvent(){
        override fun errorInfo(): String {
            return "search orders attempt failed."
        }

        override fun toString(): String {
            return "SearchOrderByReceiverStateEvent"
        }
    }

    class SearchOrderByDateEvent(
        var date : String
    ) : OrderStateEvent(){
        override fun errorInfo(): String {
            return "Search orders attempt failed."
        }

        override fun toString(): String {
            return "SearchOrderByDateStateEvent"
        }
    }

    class SetOrderNoteEvent(
        var id:Long,
        var note:String
    ) : OrderStateEvent(){
        override fun errorInfo(): String {
            return "Set order note attempt failed."
        }

        override fun toString(): String {
            return "SetOrderNoteStateEvent"
        }
    }

    class GetPastOrderEvent() : OrderStateEvent(){
        override fun errorInfo(): String {
            return "Get past orders attempt failed."
        }

        override fun toString(): String {
            return "GetPastOrderStateEvent"
        }
    }

    class None: OrderStateEvent(){
        override fun errorInfo(): String {
            return "None"
        }
    }
}