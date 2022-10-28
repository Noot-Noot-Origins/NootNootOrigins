package morgan.noot.noot.origins.cardinalComponents;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import morgan.noot.noot.origins.NootNootOriginsComponents;

interface FloatComponent extends ComponentV3 {
    float getValue();
    void setValue(float value);
}