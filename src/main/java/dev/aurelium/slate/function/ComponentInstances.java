package dev.aurelium.slate.function;

import dev.aurelium.slate.info.TemplateInfo;

@FunctionalInterface
public interface ComponentInstances<T> {

   /**
    * Gets the amount of instances of a component in a template. If greater than 1,
    * the component will be added to lore multiple times.
    *
    * @param info the {@link TemplateInfo} context object
    * @return the amount of instances to show
    */
   int getInstances(TemplateInfo<T> info);

}
