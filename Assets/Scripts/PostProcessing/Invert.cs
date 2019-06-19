using System;
using UnityEngine;
using UnityEngine.Rendering.PostProcessing;
 
[Serializable]
[PostProcess(typeof(InvertRenderer), PostProcessEvent.AfterStack, "Custom/Invert")]
public sealed class Invert : PostProcessEffectSettings
{
    [Range(0f, 1f), Tooltip("Invert effect intensity.")]
    public FloatParameter invert = new FloatParameter { value = 1f };
}
 
public sealed class InvertRenderer : PostProcessEffectRenderer<Invert>
{
    public override void Render(PostProcessRenderContext context)
    {
        var sheet = context.propertySheets.Get(Shader.Find("Hidden/Custom/Invert"));
        sheet.properties.SetFloat("_Invert", settings.invert);
        context.command.BlitFullscreenTriangle(context.source, context.destination, sheet, 0);
    }
}