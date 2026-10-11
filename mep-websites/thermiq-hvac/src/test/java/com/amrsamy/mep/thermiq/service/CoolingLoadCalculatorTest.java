package com.amrsamy.mep.thermiq.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import com.amrsamy.mep.thermiq.service.CoolingLoadCalculator.Recommendation;
import com.amrsamy.mep.thermiq.service.CoolingLoadCalculator.RoomType;
import com.amrsamy.mep.thermiq.service.CoolingLoadCalculator.SunExposure;
import com.amrsamy.mep.thermiq.service.CoolingLoadCalculator.UnitSize;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CoolingLoadCalculatorTest {

    private final CoolingLoadCalculator calculator = new CoolingLoadCalculator();

    @Test
    void typicalLivingRoomNeedsTwoAndAQuarterHorsepower() {
        Recommendation r = calculator.recommend(20, RoomType.LIVING, SunExposure.NORMAL, 2);

        assertThat(r.btu()).isEqualTo(13_000);
        assertThat(r.size()).contains(UnitSize.HP_2_25);
        assertThat(r.unitsNeeded()).isEqualTo(1);
    }

    @Test
    void shadedBedroomGetsTheSmallestUnit() {
        Recommendation r = calculator.recommend(16, RoomType.BEDROOM, SunExposure.LOW, 2);

        assertThat(r.btu()).isEqualTo(8_500);
        assertThat(r.size()).contains(UnitSize.HP_1_5);
    }

    @Test
    void extraOccupantsAddLoad() {
        Recommendation r = calculator.recommend(30, RoomType.OFFICE, SunExposure.NORMAL, 6);

        assertThat(r.btu()).isEqualTo(23_900);
        assertThat(r.size()).contains(UnitSize.HP_3);
    }

    @Test
    void largeSunnyShopNeedsSeveralUnits() {
        Recommendation r = calculator.recommend(120, RoomType.SHOP, SunExposure.HIGH, 10);

        assertThat(r.btu()).isEqualTo(112_500);
        assertThat(r.singleUnit()).isFalse();
        assertThat(r.suggestedSize()).isEqualTo(UnitSize.HP_3);
        assertThat(r.unitsNeeded()).isEqualTo(5);
    }

    @ParameterizedTest
    @CsvSource({"12000, HP_1_5", "12001, HP_2_25", "24000, HP_3", "36000, HP_5"})
    void picksTheSmallestUnitThatCoversTheLoad(int btu, UnitSize expected) {
        assertThat(CoolingLoadCalculator.sizeFor(btu)).contains(expected);
    }

    @Test
    void noSingleSplitAboveFiveHorsepower() {
        assertThat(CoolingLoadCalculator.sizeFor(36_001)).isEmpty();
    }

    @Test
    void rejectsNonPositiveInputs() {
        assertThatIllegalArgumentException().isThrownBy(() -> calculator.recommend(0, RoomType.LIVING, SunExposure.NORMAL, 1));
        assertThatIllegalArgumentException().isThrownBy(() -> calculator.recommend(10, RoomType.LIVING, SunExposure.NORMAL, 0));
    }
}
