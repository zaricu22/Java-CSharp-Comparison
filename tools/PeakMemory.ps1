# Shared helper (no param block, safe to dot-source): reads the exact peak working set (peak RAM)
# of a process from Windows, also after it has exited, via GetProcessMemoryInfo on its handle.
if (-not ('PeakMemory' -as [type])) {
    Add-Type @"
using System;
using System.Runtime.InteropServices;
public static class PeakMemory {
    [StructLayout(LayoutKind.Sequential)]
    struct PROCESS_MEMORY_COUNTERS {
        public uint cb; public uint PageFaultCount;
        public UIntPtr PeakWorkingSetSize; public UIntPtr WorkingSetSize;
        public UIntPtr QuotaPeakPagedPoolUsage; public UIntPtr QuotaPagedPoolUsage;
        public UIntPtr QuotaPeakNonPagedPoolUsage; public UIntPtr QuotaNonPagedPoolUsage;
        public UIntPtr PagefileUsage; public UIntPtr PeakPagefileUsage;
    }
    [DllImport("psapi.dll", SetLastError = true)]
    static extern bool GetProcessMemoryInfo(IntPtr process, out PROCESS_MEMORY_COUNTERS counters, uint size);
    public static long PeakWorkingSet(IntPtr process) {
        PROCESS_MEMORY_COUNTERS c;
        if (!GetProcessMemoryInfo(process, out c, (uint)Marshal.SizeOf(typeof(PROCESS_MEMORY_COUNTERS)))) return -1;
        return (long)c.PeakWorkingSetSize.ToUInt64();
    }
}
"@
}

function Get-Median([object[]] $values) {
    $sorted = @($values | Sort-Object)
    return $sorted[[int][math]::Floor($sorted.Count / 2)]
}
