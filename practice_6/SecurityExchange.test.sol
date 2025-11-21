// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "remix_tests.sol";
import "./Security.sol";
import "./Exchange.sol";

/**
 * @title SecurityExchangeTest
 * @dev Unit-тесты для проверки функциональности
 */
contract SecurityExchangeTest {
    Security security;
    Exchange exchange;

    address acc0 = address(this);  // Адрес контракта = владелец

    /// Тест создания ценной бумаги
    function testSecurityCreation() public {
        security = new Security("Gazprom", "GAZP", 1000);
        Assert.equal(security.name(), "Gazprom", "Название должно быть Gazprom");
        Assert.equal(security.symbol(), "GAZP", "Символ должен быть GAZP");
        Assert.equal(security.totalSupply(), 1000, "Общий выпуск должен быть 1000");
    }

    /// Тест баланса создателя
    function testInitialBalance() public {
        Assert.equal(
            security.balanceOf(acc0),
            1000,
            "У создателя должно быть 1000 бумаг"
        );
    }

    /// Тест перевода бумаг
    function testTransfer() public {
        address recipient = address(0x123);
        security.transfer(recipient, 100);

        Assert.equal(
            security.balanceOf(acc0),
            900,
            "У отправителя должно остаться 900 бумаг"
        );
        Assert.equal(
            security.balanceOf(recipient),
            100,
            "У получателя должно быть 100 бумаг"
        );
    }

    /// Тест создания биржи
    function testExchangeCreation() public {
        exchange = new Exchange();
        Assert.equal(exchange.orderCount(), 0, "Изначально ордеров нет");
    }

    /// Тест разрешения на перевод
    function testApproval() public {
        security.approve(address(exchange), 500);
        Assert.equal(
            security.allowance(acc0, address(exchange)),
            500,
            "Разрешение должно быть на 500 бумаг"
        );
    }
}
