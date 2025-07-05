from Trade_pb2 import Trade, NewOrder, Cancel, Amend


class MessageBuilder:
    def build_new_order(self, order_id: str, symbol: str, quantity: int, price: float) -> bytes:
        trade = Trade()
        new_order = NewOrder(
            order_id=order_id,
            symbol=symbol,
            quantity=quantity,
            price=price
        )
        trade.new_order.CopyFrom(new_order)
        return trade.SerializeToString()

    def build_cancel(self, order_id: str) -> bytes:
        trade = Trade()
        cancel = Cancel(order_id=order_id)
        trade.cancel.CopyFrom(cancel)
        return trade.SerializeToString()

    def build_amend(self, order_id: str, new_quantity: int, new_price: float) -> bytes:
        trade = Trade()
        amend = Amend(
            order_id=order_id,
            new_quantity=new_quantity,
            new_price=new_price
        )
        trade.amend.CopyFrom(amend)
        return trade.SerializeToString()
