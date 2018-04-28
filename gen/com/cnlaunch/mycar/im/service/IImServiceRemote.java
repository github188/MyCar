/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\work\\MyCarAndroid\\Code\\src\\com\\cnlaunch\\mycar\\im\\service\\IImServiceRemote.aidl
 */
package com.cnlaunch.mycar.im.service;
public interface IImServiceRemote extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.cnlaunch.mycar.im.service.IImServiceRemote
{
private static final java.lang.String DESCRIPTOR = "com.cnlaunch.mycar.im.service.IImServiceRemote";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.cnlaunch.mycar.im.service.IImServiceRemote interface,
 * generating a proxy if needed.
 */
public static com.cnlaunch.mycar.im.service.IImServiceRemote asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.cnlaunch.mycar.im.service.IImServiceRemote))) {
return ((com.cnlaunch.mycar.im.service.IImServiceRemote)iin);
}
return new com.cnlaunch.mycar.im.service.IImServiceRemote.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_isLogined:
{
data.enforceInterface(DESCRIPTOR);
boolean _result = this.isLogined();
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.cnlaunch.mycar.im.service.IImServiceRemote
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public boolean isLogined() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_isLogined, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_isLogined = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public boolean isLogined() throws android.os.RemoteException;
}
