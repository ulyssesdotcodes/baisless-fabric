using System;
using UnityEngine;
using UnityEngine.Rendering.PostProcessing;
 
[Serializable]
[PostProcess(typeof(GlitchRenderer), PostProcessEvent.AfterStack, "Custom/AnalogGlitch")]
public sealed class Glitch : PostProcessEffectSettings
{
    [Tooltip("scan line jitter"), Range(0,1)]
    public FloatParameter scanLine = new FloatParameter { value = 0f };

    [Tooltip("vertical jump"), Range(0,1)]
    public FloatParameter verticalJump = new FloatParameter { value = 0f };
    [Tooltip("Horizontal shake"), Range(0,1)]
    public FloatParameter horizontalShake = new FloatParameter { value = 0f };
    [Tooltip("Color drift"), Range(0,1)]
    public FloatParameter colorDrift = new FloatParameter { value = 0f };
}
 
public sealed class GlitchRenderer : PostProcessEffectRenderer<Glitch>
{
    float _verticalJumpTime;
    public override void Render(PostProcessRenderContext context)
    {
        var sheet = context.propertySheets.Get(Shader.Find("Hidden/Kino/Glitch/Analog"));
        float verticalJump = settings.verticalJump.value;
        float scanline = settings.scanLine.value;
        _verticalJumpTime += Time.deltaTime * verticalJump * 11.3f;

        var sl_thresh = Mathf.Clamp01(1.0f - scanline * 1.2f);
        var sl_disp = 0.002f + Mathf.Pow(scanline, 3) * 0.05f;
        sheet.properties.SetVector("_ScanLineJitter", new Vector2(sl_disp, sl_thresh));

        var vj = new Vector2(verticalJump, _verticalJumpTime);
        sheet.properties.SetVector("_VerticalJump", vj);
        sheet.properties.SetFloat("_HorizontalShake", settings.horizontalShake * 0.2f);
        var cd = new Vector2(settings.colorDrift.value * 0.04f, Time.time * 606.11f);
        sheet.properties.SetVector("_ColorDrift", cd);

        context.command.BlitFullscreenTriangle(context.source, context.destination, sheet, 0);
    }
}