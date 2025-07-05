#include "Trade.pb.h"

class TradeMessageBuilder {
public:
    static fixprocessor::Trade NewOrder(const std::string& order_id, const std::string& symbol, int quantity, double price) {
        fixprocessor::Trade trade;
        auto* new_order = trade.mutable_new_order();
        new_order->set_order_id(order_id);
        new_order->set_symbol(symbol);
        new_order->set_quantity(quantity);
        new_order->set_price(price);
        return trade;
    }

    static fixprocessor::Trade Cancel(const std::string& order_id) {
        fixprocessor::Trade trade;
        auto* cancel = trade.mutable_cancel();
        cancel->set_order_id(order_id);
        return trade;
    }

    static fixprocessor::Trade Amend(const std::string& order_id, int new_quantity, double new_price) {
        fixprocessor::Trade trade;
        auto* amend = trade.mutable_amend();
        amend->set_order_id(order_id);
        amend->set_new_quantity(new_quantity);
        amend->set_new_price(new_price);
        return trade;
    }
}; 