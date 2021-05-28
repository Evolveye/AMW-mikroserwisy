package pl.gdynia.amw.lab6.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.gdynia.amw.lab6.service.MainService;
import pl.gdynia.amw.lab6.model.Exchange;
import pl.gdynia.amw.lab6.model.Rate;
import pl.gdynia.amw.lab6.response.exception.WrongCurrencyException;
import pl.gdynia.amw.lab6.response.success.ExchangeRateCalcSuccess;

import java.math.BigDecimal;

@RestController
public class MainController {
    @Autowired
    MainService repo;

    @GetMapping("/exchange/rate/{currencyTo}")
    ExchangeRateCalcSuccess exchangeRate(@PathVariable String currencyTo) {
        String currencyFromUpper = currencyTo.toUpperCase();

        Exchange exchange = repo.apiCommunicator.getLastApiResponse().getLastExchange();
        Rate rateObj = exchange.getRateByCurrency(currencyFromUpper);

        if (rateObj == null) {
            throw new WrongCurrencyException(currencyFromUpper);
        }

        BigDecimal rate = repo.calculator.calculateExchange(rateObj);

        return ExchangeRateCalcSuccess.newSuccess("EUR",currencyFromUpper, rate);
    }

    @GetMapping("/exchange/rate/{currencyFrom}/{currencyTo}")
    ExchangeRateCalcSuccess exchangeRate(@PathVariable String currencyFrom, @PathVariable String currencyTo) {
        String currencyFromUpper = currencyFrom.toUpperCase();
        String currencyToUpper = currencyTo.toUpperCase();

        Exchange exchange = repo.apiCommunicator.getLastApiResponse().getLastExchange();
        Rate rateFromObj = exchange.getRateByCurrency(currencyFromUpper);
        Rate rateToObj = exchange.getRateByCurrency(currencyToUpper);

        if (rateFromObj == null) {
            throw new WrongCurrencyException(currencyFromUpper);
        }

        if (rateToObj == null) {
            throw new WrongCurrencyException(currencyFromUpper);
        }

        BigDecimal rate = repo.calculator.calculateExchange(rateFromObj, rateToObj);

        return ExchangeRateCalcSuccess.newSuccess(currencyFromUpper, currencyToUpper, rate);
    }
}