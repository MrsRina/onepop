package rina.onepop.club.client.component;

import rina.onepop.club.api.component.Component;
import rina.onepop.club.api.component.StringType;

/**
 * @author SrRina
 * @since 06/04/2021 at 14:02
 **/
public class ComponentRinaMessage extends Component {
    final private String[] specialThanksAndMuchTexto = new String[] {
            "Hi, thank you for buy my client!",
            "Im very happy, because, all this GUI, HUD & Auto Crystal,",
            " are very well made to wrong places...",
            "Now its on right place! Enjoy the client!!",
            "Its helping me to go live for Canada!",
            "Special thanks to Jake, Hero and all users and members of 1POP!"
    };

    public ComponentRinaMessage() {
        super("Rina Message", "RinaMessage", "Rina message!", StringType.USE);
    }

    @Override
    public void onRender(float partialTicks) {
        int stringWidth = 1;
        int stringHeight = 1;

        for (String lines : this.specialThanksAndMuchTexto) {
            this.render(lines, 0, stringHeight);

            if (this.getStringWidth(lines) > stringWidth) {
                stringWidth = this.getStringWidth(lines);
            }

            stringHeight += this.getStringHeight(lines) + 1;
        }

        this.rect.setWidth(stringWidth);
        this.rect.setHeight(stringHeight);
    }
}
