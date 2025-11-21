// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

/**
 * @title Security
 * @dev Простой контракт ценной бумаги (токен)
 */
contract Security {
    string public name;           // Название ценной бумаги
    string public symbol;         // Символ (тикер)
    uint256 public totalSupply;   // Общее количество

    mapping(address => uint256) public balanceOf;  // Балансы владельцев

    event Transfer(address indexed from, address indexed to, uint256 value);

    /**
     * @dev Конструктор - создаём ценные бумаги
     */
    constructor(
        string memory _name,
        string memory _symbol,
        uint256 _initialSupply
    ) {
        name = _name;
        symbol = _symbol;
        totalSupply = _initialSupply;
        balanceOf[msg.sender] = _initialSupply;  // Все бумаги создателю
    }

    /**
     * @dev Перевод ценных бумаг
     */
    function transfer(address _to, uint256 _value) public returns (bool) {
        require(_to != address(0), "Некорректный адрес");
        require(balanceOf[msg.sender] >= _value, "Недостаточно бумаг");

        balanceOf[msg.sender] -= _value;
        balanceOf[_to] += _value;

        emit Transfer(msg.sender, _to, _value);
        return true;
    }

    /**
     * @dev Разрешение на перевод от имени владельца
     */
    mapping(address => mapping(address => uint256)) public allowance;

    function approve(address _spender, uint256 _value) public returns (bool) {
        allowance[msg.sender][_spender] = _value;
        return true;
    }

    function transferFrom(address _from, address _to, uint256 _value) public returns (bool) {
        require(_to != address(0), "Некорректный адрес");
        require(balanceOf[_from] >= _value, "Недостаточно бумаг");
        require(allowance[_from][msg.sender] >= _value, "Нет разрешения");

        balanceOf[_from] -= _value;
        balanceOf[_to] += _value;
        allowance[_from][msg.sender] -= _value;

        emit Transfer(_from, _to, _value);
        return true;
    }
}