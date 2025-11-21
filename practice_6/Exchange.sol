// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "./Security.sol";

/**
 * @title Exchange
 * @dev Простая биржа для торговли ценными бумагами
 */
contract Exchange {

    struct Order {
        uint256 id;
        address trader;
        address securityToken;  // Адрес контракта ценной бумаги
        uint256 amount;         // Количество бумаг
        uint256 price;          // Цена за одну бумагу (в wei)
        bool isBuyOrder;        // true = покупка, false = продажа
        bool isActive;          // Активен ли ордер
    }

    uint256 public orderCount = 0;
    mapping(uint256 => Order) public orders;

    event OrderCreated(
        uint256 id,
        address trader,
        address securityToken,
        uint256 amount,
        uint256 price,
        bool isBuyOrder
    );

    event OrderExecuted(
        uint256 orderId,
        address buyer,
        address seller,
        uint256 amount,
        uint256 price
    );

    event OrderCancelled(uint256 orderId);

    /**
     * @dev Создать ордер на покупку
     */
    function createBuyOrder(
        address _securityToken,
        uint256 _amount,
        uint256 _price
    ) public payable {
        require(_amount > 0, "Количество должно быть > 0");
        require(_price > 0, "Цена должна быть > 0");
        require(msg.value >= _amount * _price, "Недостаточно средств");

        orderCount++;
        orders[orderCount] = Order(
            orderCount,
            msg.sender,
            _securityToken,
            _amount,
            _price,
            true,  // Это ордер на покупку
            true   // Активен
        );

        emit OrderCreated(orderCount, msg.sender, _securityToken, _amount, _price, true);
    }

    /**
     * @dev Создать ордер на продажу
     */
    function createSellOrder(
        address _securityToken,
        uint256 _amount,
        uint256 _price
    ) public {
        require(_amount > 0, "Количество должно быть > 0");
        require(_price > 0, "Цена должна быть > 0");

        Security security = Security(_securityToken);
        require(
            security.allowance(msg.sender, address(this)) >= _amount,
            "Нет разрешения на перевод бумаг"
        );

        orderCount++;
        orders[orderCount] = Order(
            orderCount,
            msg.sender,
            _securityToken,
            _amount,
            _price,
            false,  // Это ордер на продажу
            true    // Активен
        );

        emit OrderCreated(orderCount, msg.sender, _securityToken, _amount, _price, false);
    }

    /**
     * @dev Исполнить ордер на покупку (продать ему бумаги)
     */
    function executeBuyOrder(uint256 _orderId) public {
        Order storage order = orders[_orderId];
        require(order.isActive, "Ордер неактивен");
        require(order.isBuyOrder, "Это не ордер на покупку");

        Security security = Security(order.securityToken);
        require(
            security.allowance(msg.sender, address(this)) >= order.amount,
            "Нет разрешения на перевод бумаг"
        );

        // Переводим бумаги продавца покупателю
        security.transferFrom(msg.sender, order.trader, order.amount);

        // Переводим деньги продавцу
        uint256 totalPrice = order.amount * order.price;
        payable(msg.sender).transfer(totalPrice);

        order.isActive = false;

        emit OrderExecuted(_orderId, order.trader, msg.sender, order.amount, order.price);
    }

    /**
     * @dev Исполнить ордер на продажу (купить у него бумаги)
     */
    function executeSellOrder(uint256 _orderId) public payable {
        Order storage order = orders[_orderId];
        require(order.isActive, "Ордер неактивен");
        require(!order.isBuyOrder, "Это не ордер на продажу");

        uint256 totalPrice = order.amount * order.price;
        require(msg.value >= totalPrice, "Недостаточно средств");

        Security security = Security(order.securityToken);

        // Переводим бумаги от продавца к покупателю
        security.transferFrom(order.trader, msg.sender, order.amount);

        // Переводим деньги продавцу
        payable(order.trader).transfer(totalPrice);

        order.isActive = false;

        emit OrderExecuted(_orderId, msg.sender, order.trader, order.amount, order.price);
    }

    /**
     * @dev Отменить свой ордер
     */
    function cancelOrder(uint256 _orderId) public {
        Order storage order = orders[_orderId];
        require(order.trader == msg.sender, "Это не ваш ордер");
        require(order.isActive, "Ордер уже неактивен");

        order.isActive = false;

        // Если это был ордер на покупку, возвращаем деньги
        if (order.isBuyOrder) {
            uint256 totalPrice = order.amount * order.price;
            payable(msg.sender).transfer(totalPrice);
        }

        emit OrderCancelled(_orderId);
    }

    /**
     * @dev Получить баланс контракта
     */
    function getBalance() public view returns (uint256) {
        return address(this).balance;
    }
}
