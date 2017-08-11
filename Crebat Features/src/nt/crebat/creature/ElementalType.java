/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nt.crebat.creature;

import java.util.Arrays;
import java.util.Map;
import nt.crebat.utils.NamedTableObject;
import nt.ps.lang.PSFunction;
import nt.ps.lang.PSObject;
import nt.ps.lang.PSValue;

/**
 *
 * @author Asus
 */
public final class ElementalType extends NamedTableObject<ElementalType>
{
    private final int[] weaknesses;
    private final int[] strengths;
    private final int[] immunities;
    
    private ElementalType(PSObject object)
    {
        super(object);
        weaknesses = mapToInts(object.getProperty("weaknesses"));
        strengths = mapToInts(object.getProperty("strengths"));
        immunities = mapToInts(object.getProperty("immunities"));
    }
    
    private static int[] mapToInts(PSValue value)
    {
        int[] array = value.toJavaList().stream().mapToInt(v -> v.toJavaInt()).toArray();
        Arrays.sort(array);
        return array;
    }
    
    /**
     * Calcula la efectividad del tipo representado por este objeto contra el tipo {@code type}.
     * @param type
     * @return 
     */
    public final float computeAttackEffectivity(ElementalType type) { return type.computeDefenseEffectivity(this); }
    
    /**
     * Calcula la efectividad del tipo representado por {@code type} contra el tipo representado por este objeto.
     * @param type
     * @return 
     */
    public final float computeDefenseEffectivity(ElementalType type)
    {
        int id = type.getId();
        return Arrays.binarySearch(weaknesses, id) >= 0 ? 2f
                : Arrays.binarySearch(strengths, id) >= 0 ? 0.5f
                : Arrays.binarySearch(immunities, id) >= 0 ? 0f
                : 1f;
    }
    
    public final boolean isWeakTo(ElementalType type)
    {
        return Arrays.binarySearch(weaknesses, type.getId()) >= 0;
    }
    
    public final boolean isResistantTo(ElementalType type)
    {
        return Arrays.binarySearch(strengths, type.getId()) >= 0;
    }
    
    public final boolean isImmuneTo(ElementalType type)
    {
        return Arrays.binarySearch(immunities, type.getId()) >= 0;
    }

    @Override
    protected final PSValue innerGetProperty(String name)
    {
        switch(name)
        {
            default: return UNDEFINED;
            case "computeAttackEffectivity": return COMPUTE_ATTACK_EFFECTIVITY;
            case "computeDefenseEffectivity": return COMPUTE_DEFENSE_EFFECTIVITY;
            case "isWeakTo": return IS_WEAK_TO;
            case "isResistantTo": return IS_RESISTANT_TO;
            case "isImmuneTo": return IS_IMMUNE_TO;
        }
    }
    
    private static final Map<Integer, ElementalType> HASH = importTable("elementalTypes", ElementalType.class);
    
    private static final PSValue
            COMPUTE_ATTACK_EFFECTIVITY = PSFunction.<ElementalType>method((self, arg0) -> {
                if(arg0.isNumber())
                    return valueOf(self.computeAttackEffectivity(HASH.get(arg0.toJavaInt())));
                return valueOf(self.computeAttackEffectivity(arg0.toPSUserdata()));
            }),
            COMPUTE_DEFENSE_EFFECTIVITY = PSFunction.<ElementalType>method((self, arg0) -> {
                if(arg0.isNumber())
                    return valueOf(self.computeDefenseEffectivity(HASH.get(arg0.toJavaInt())));
                return valueOf(self.computeDefenseEffectivity(arg0.toPSUserdata()));
            }),
            IS_WEAK_TO = PSFunction.<ElementalType>method((self, arg0) -> {
                if(arg0.isNumber())
                    return valueOf(self.isWeakTo(HASH.get(arg0.toJavaInt())));
                return valueOf(self.isWeakTo(arg0.toPSUserdata()));
            }),
            IS_RESISTANT_TO = PSFunction.<ElementalType>method((self, arg0) -> {
                if(arg0.isNumber())
                    return valueOf(self.isResistantTo(HASH.get(arg0.toJavaInt())));
                return valueOf(self.isResistantTo(arg0.toPSUserdata()));
            }),
            IS_IMMUNE_TO = PSFunction.<ElementalType>method((self, arg0) -> {
                if(arg0.isNumber())
                    return valueOf(self.isImmuneTo(HASH.get(arg0.toJavaInt())));
                return valueOf(self.isImmuneTo(arg0.toPSUserdata()));
            });
}
